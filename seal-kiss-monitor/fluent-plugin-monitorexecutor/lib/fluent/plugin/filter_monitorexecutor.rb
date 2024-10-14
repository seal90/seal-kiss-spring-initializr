# -*- coding: utf-8 -*-

require 'fluent/plugin/filter'
require 'json'

module Fluent::Plugin
  class MonitorExecutorFilter < Filter
    Fluent::Plugin.register_filter('monitorexecutor', self)
    
    config_param :tag, :string, :default => 'monitor_executor'

    config_section :rule, param_name: :rule_configs, multi: true, required: true do
      config_param :rule_id, :string, default: nil
      config_param :service_name, :string, default: nil
      config_param :and_or, :enum, list: [:and, :or], default: :and
      config_section :api_rule, param_name: :api_rule_configs, multi: true, required: true do
        config_param :minutes, :integer, default: 1
        config_param :api, :string, default: nil
        config_param :metric_type, :enum, list: [:num, :success_num, :fail_num, :success_rate, :fail_rate, :avg_realtime, :p95_realtime, :p99_realtime], default: :num
        config_param :aggregation_type, :enum, list: [:average, :continued], default: :average 
        config_param :compare_rule, :enum, list: [:greater, :equal, :less], default: :greater
        config_param :compare_val, :float, default: 0
      end
    end

    attr_accessor :api_aggregate
    attr_accessor :watch_start

    def configure(conf)
      super
  
      @api_aggregate = {}
      @api_time = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00'))
      @mutex = Mutex.new
      @watch_start = true
    end
  
    def start
      super
      start_watch
    end
  
    def shutdown
      super
      if watch_start
        @watcher.terminate
        @watcher.join
      end
    end
  
    def start_watch
      if watch_start
        @watcher = Thread.new(&method(:watch))
      end
    end
  
    def watch
      # every minute
      while true
        star_time = Time.now
        end_time = Time.new(star_time.strftime('%Y-%m-%d %H:%M:01')) + 60
        to_wait_sec = end_time - star_time
        
        sleep to_wait_sec.to_f
        match_rule = rule_exec(@api_time)
        flush_emit(match_rule)
        @api_time = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00'))
      end
    end

    def rule_exec(time) 
      service_match_rule = Array.new
      @rule_configs.each{|service_rule|
        rule_id_val = service_rule['rule_id']
        service_name_val = service_rule['service_name']
        and_or_val = service_rule['and_or']

        api_rules = service_rule['api_rule_configs']
        api_rules_match = Array.new
        api_data_missing_flag = false
        last_fail_trace = ''
        api_rules.each{|api_rule_val|
          minutes_val = api_rule_val['minutes']
          api_val = api_rule_val['api']
          metric_type_val = api_rule_val['metric_type']
          aggregation_type_val = api_rule_val['aggregation_type']
          compare_rule_val = api_rule_val['compare_rule']
          compare_val_val = api_rule_val['compare_val']

          aggregate_val = @api_aggregate[service_name_val + "_" + api_val]
          if nil == aggregate_val || aggregate_val.size < minutes_val
            api_data_missing_flag = true
            break
          else
            # metric_aggregates = aggregate_val.slice(-minutes_val, minutes_val)
            end_time = time;
            start_time = end_time - (minutes_val*60)
            metric_aggregates = aggregate_val.select{|item| Time.new(item['time']) > start_time && Time.new(item['time']) <= end_time}
            if metric_aggregates.size < minutes_val
              api_data_missing_flag = true
              break
            end
            metric_values = metric_aggregates.map{|item| item[metric_type_val.to_s].to_f}
            match_flag = false
            case aggregation_type_val
            when :average
              avg_metric_value = metric_values.inject(0.00) { |sum, el| sum + el }
              match_flag = compare_metric_val(avg_metric_value, compare_rule_val, compare_val_val)
            when :continued
              match_flag = metric_values.all?{|item| compare_metric_val(item, compare_rule_val, compare_val_val)}
            end
            last_fail_aggregate = metric_aggregates.reject{|item| 
              trace_id = item['last_fail_trace']
              trace_id == nil || trace_id.empty?
            }.last
            if nil != last_fail_aggregate 
              last_fail_trace = last_fail_aggregate['last_fail_trace']
            end
            api_rules_match.push(match_flag)
          end
        }

        if !api_data_missing_flag
          service_rule_match = false
          case and_or_val
          when :and
            service_rule_match = api_rules_match.all?
          when :or
            service_rule_match = api_rules_match.any?
          end

          if service_rule_match 
            service_match_rule.push({"time" => time, "rule_id" => rule_id_val, "service_name" => service_name_val, "last_fail_trace" => last_fail_trace})
          end
        end

      }
      service_match_rule
    end
  
    def flush_emit(match_rule)
      match_rule.each{|val|
        router.emit(@tag, val['time'], JSON.generate(val))
      }
    end

    def compare_metric_val(metric_val, compare_rule, compare_val) 
      case compare_rule
      when :greater
        return metric_val > compare_val
      when :equal
        return metric_val = compare_val
      when :less
        return metric_val < compare_val
      end
    end
  
    def filter(tag, time, record)
      @mutex.lock
      begin
        aggregate = JSON.parse(record)
        service_name = aggregate["service_name"]
        api = aggregate["api"]

        aggregate_array = @api_aggregate[service_name + "_" + api]
        if nil == aggregate_array
          aggregate_array = Array.new
          @api_aggregate[service_name + "_" + api] = aggregate_array
        end
        aggregate_array.push(aggregate)
        while aggregate_array.size > 10 do
          aggregate_array.shift
        end
        
      rescue Exception => e
        log.warn(e.message)
      ensure
        @mutex.unlock
      end
      nil
    end
  end
  
  class ApiAggregate
  
    attr_accessor :time
    attr_accessor :service_name
    attr_accessor :api
    attr_accessor :num
    attr_accessor :success_num
    attr_accessor :fail_num
    attr_accessor :success_rate
    attr_accessor :fail_rate
    attr_accessor :avg_realtime
    attr_accessor :p95_realtime
    attr_accessor :p99_realtime
    attr_accessor :last_fail_trace
  
    def initialize(time, service_name, api)
      @time = time
      @service_name = service_name
      @api = api
      @num = 0
      @success_num = 0
      @fail_num = 0
      @success_rate = 1.0
      @fail_rate = 0.0
      @avg_realtime = 0
      @p95_realtime = 0
      @p99_realtime = 0
      @last_fail_trace = ''
    end
  
    def to_json(*a)
      to_hash.to_json(*a)
    end
  
    def to_hash
      hash = {}
      instance_variables.each { |var| hash[var.to_s.delete('@')] = instance_variable_get(var) }
      hash
    end
  end
end
