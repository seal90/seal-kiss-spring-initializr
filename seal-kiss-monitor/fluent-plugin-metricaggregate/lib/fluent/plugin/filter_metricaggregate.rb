# -*- coding: utf-8 -*-

require 'fluent/plugin/filter'
require 'json'

module Fluent::Plugin
  class MetricAggregateFilter < Filter
    Fluent::Plugin.register_filter('metricaggregate', self)
    
    config_section :metric, param_name: :metric_configs, multi: true, required: true do
      config_param :metric_rule_id, :string
      config_param :service_name, :string
      config_param :code, :string
      config_param :sub_code, :string, default: nil
    end

    config_param :tag, :string, :default => 'metric_aggregate'
  
    attr_accessor :metric_counter
    attr_accessor :watch_start

    def configure(conf)
      super
      @metric_counter = {}
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
        end_time = Time.new(star_time.strftime('%Y-%m-%d %H:%M:00')) + 60
        to_wait_sec = end_time - star_time
        
        sleep to_wait_sec.to_f
        flush_emit
      end
    end
  
    def flush_emit
      @mutex.lock
      @metric_time = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00'))
      emit_counter = @metric_counter
      @metric_counter = {}
      @mutex.unlock

      emit_counter.each_value{|val| 
        aggregate = val.calc_aggregate
        router.emit(@tag, aggregate.time, JSON.generate(aggregate))
      }
    end
  
    def filter(tag, time, record)
      @mutex.lock
      begin
        # 2024-10-18T15:20:56.557+08:00  INFO 5772 --- [seal-kiss-multi] [http-nio-8080-exec-1] [557b69ef240e76aabc7639d2dfbd2bcd-d0bc7d0f2930b3a3] i.github.seal90.kiss.core.log.MetricLog  : {"code":"TEST_METRIC_1","subCode":"TEST_METRIC_SUB_1","num":1,"msg":"测试metric"} 
        # https://rubular.com/
        metric_regex = /.+\-\-\- \[(.+)\] \[.+\].+\[(.+)\].+?: (\{.+)/
        metric_regex_val = metric_regex.match(record["message"])
        if nil != metric_regex_val 
          service_name = metric_regex_val[1]
          metric_val = JSON.parse(metric_regex_val[3])

          match_configs = metric_configs.select{|config| config.service_name == service_name && config["code"] = metric_val["code"]}
          if nil != match_configs 
            match_configs.each do |config|
              key = service_name + "_" + config["code"]
              if nil != config["sub_code"]
                key = key + '_' + config["sub_code"]
              end
              counter = @metric_counter[key]
              if nil == counter 
                counter = MetricCounter::new(@metric_time, service_name, config["code"], config["sub_code"], config["metric_rule_id"])
                @metric_counter[key] = counter
              end
              counter.count(metric_val)
            end
          end
        end
      rescue Exception => e
        log.warn(e.message)
      ensure
        @mutex.unlock
      end
      nil
    end
  end

  class MetricCounter

    attr_accessor :time
    attr_accessor :service_name
    attr_accessor :code
    attr_accessor :sub_code
    attr_accessor :metric_rule_id
    attr_accessor :num
    
    def initialize(time, service_name, code, sub_code, metric_rule_id)
      @time = time
      @service_name = service_name
      @code = code
      @sub_code = sub_code
      @metric_rule_id = metric_rule_id
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
  
    def count(metric)
      num = metric["num"]
      if nil == num 
        @num += 1
      else
        @num += num
      end
    end
  
    def calc_aggregate
      aggregate = MetricAggregate.new(@time, @service_name, @code, @sub_code)
      aggregate.metric_rule_id = @metric_rule_id
      aggregate.num = @num
      aggregate
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
