# -*- coding: utf-8 -*-

require 'fluent/plugin/filter'
require 'json'

module Fluent::Plugin
  class MetricMonitorFilter < Filter
    Fluent::Plugin.register_filter('metricmonitor', self)
    
    config_param :tag, :string, :default => 'metric_monitor'

    config_section :rule, param_name: :rule_configs, multi: true, required: true do
      config_param :monitor_rule_id, :string
      config_param :metric_rule_id, :string
      config_param :and_or, :enum, list: [:and, :or], default: :and
      config_section :metric_rule, param_name: :metric_rule_configs, multi: true, required: true do
        config_param :minutes, :integer, default: 1
        config_param :aggregation_type, :enum, list: [:average, :continued], default: :average 
        config_param :compare_rule, :enum, list: [:greater, :equal, :less], default: :greater
        config_param :compare_val, :float, default: 0
      end
    end

    attr_accessor :metric_aggregate
    attr_accessor :watch_start

    def configure(conf)
      super
  
      @metric_aggregate = {}
      @metric_time = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00'))
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
        match_rule = rule_exec(@metric_time)
        flush_emit(match_rule)
        @metric_time = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00'))
      end
    end

    def rule_exec(time) 
      match_rule = Array.new
      @rule_configs.each{|service_rule|
        metric_rule_id_val = service_rule['metric_rule_id']
        monitor_rule_id_val = service_rule['monitor_rule_id']
        and_or_val = service_rule['and_or']

        metric_rules = service_rule['metric_rule_configs']
        metric_rules_match = Array.new
        metric_data_missing_flag = false
        metric_rules.each{|metric_rule_val|
          minutes_val = metric_rule_val['minutes']
          aggregation_type_val = metric_rule_val['aggregation_type']
          compare_rule_val = metric_rule_val['compare_rule']
          compare_val_val = metric_rule_val['compare_val']

          aggregate_val = @metric_aggregate[metric_rule_id_val]
          if nil == aggregate_val || aggregate_val.size < minutes_val
            metric_data_missing_flag = true
            break
          else
            # metric_aggregates = aggregate_val.slice(-minutes_val, minutes_val)
            end_time = time;
            start_time = end_time - (minutes_val*60)
            metric_aggregates = aggregate_val.select{|item| Time.new(item['time']) > start_time && Time.new(item['time']) <= end_time}
            if metric_aggregates.size < minutes_val
              metric_data_missing_flag = true
              break
            end
            metric_values = metric_aggregates.map{|item| item["num"].to_f}
            match_flag = false
            case aggregation_type_val
            when :average
              avg_metric_value = metric_values.inject(0.00) { |sum, el| sum + el }
              match_flag = compare_metric_val(avg_metric_value, compare_rule_val, compare_val_val)
            when :continued
              match_flag = metric_values.all?{|item| compare_metric_val(item, compare_rule_val, compare_val_val)}
            end
            metric_rules_match.push(match_flag)
          end
        }

        if !metric_data_missing_flag
          rule_match = false
          case and_or_val
          when :and
            rule_match = metric_rules_match.all?
          when :or
            rule_match = metric_rules_match.any?
          end

          if rule_match 
            match_rule.push({"time" => time, "monitor_rule_id" => monitor_rule_id_val})
          end
        end

      }
      match_rule
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
        metric_rule_id = aggregate["metric_rule_id"]

        aggregate_array = @metric_aggregate[metric_rule_id]
        if nil == aggregate_array
          aggregate_array = Array.new
          @metric_aggregate[metric_rule_id] = aggregate_array
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
  
  class MetricAggregate
  
    attr_accessor :time
    attr_accessor :service_name
    attr_accessor :code
    attr_accessor :sub_code
    attr_accessor :metric_rule_id
    attr_accessor :num
  
    def initialize(time, service_name, code, sub_code)
      @time = time
      @service_name = service_name
      @code = code
      @sub_code = sub_code
      @num = 0
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
