# -*- coding: utf-8 -*-

require 'fluent/plugin/filter'
require 'json'

module Fluent::Plugin
  class ApiAggregateFilter < Filter
    Fluent::Plugin.register_filter('apiaggregate', self)
    
    config_param :origin, :string, :default => nil
    config_param :tag, :string, :default => 'api_aggregate'
  
    attr_accessor :api_counter
    attr_accessor :watch_start

    def configure(conf)
      super
      @api_counter = {}
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
        end_time = Time.new(star_time.strftime('%Y-%m-%d %H:%M:00')) + 60
        to_wait_sec = end_time - star_time
        
        sleep to_wait_sec.to_f
        flush_emit
      end
    end
  
    def flush_emit
      @mutex.lock
      @api_time = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00'))
      emit_counter = @api_counter
      @api_counter = {}
      @mutex.unlock

      emit_counter.each_value{|val| 
        aggregate = val.calc_aggregate
        router.emit(@tag, aggregate.time, JSON.generate(aggregate))
      }
    end
  
    def filter(tag, time, record)
      @mutex.lock
      begin
        # 2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d9] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":420,"bsf":"Y","ntf":"Y"}
        # https://rubular.com/
        metric_regex = /.+\-\-\- \[(.+)\] \[.+\].+\[(.+)\].+?: (\{.+)/
        metric_regex_val = metric_regex.match(record["message"])
        if nil != metric_regex_val 
          service_name = metric_regex_val[1]
          tid = metric_regex_val[2]
          metric_val = JSON.parse(metric_regex_val[3])
          metric_val["tid"] = tid
          if @origin == metric_val["o"]
            api = metric_val["id"]
            counter = @api_counter[service_name + "_" +api]
            if nil == counter 
              counter = ApiCounter::new(@api_time, service_name, api)
              @api_counter[service_name + "_" +api] = counter
            end
            counter.count(metric_val)
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

  class ApiCounter

    def initialize(time, service_name, api)
      @time = time
      @service_name = service_name
      @api = api
      @num = 0
      @success_num = 0
      @fail_num = 0
      @realtimes = []
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

    def calc_success_rate
      if 0 == @num
        1.0
      else
        sprintf("%.2f", @success_num.to_f / @num).to_f
      end
    end
  
    def calc_fail_rate
      if 0 == @num
        0.0
      else
        sprintf("%.2f", @fail_num.to_f / @num).to_f
      end
    end
  
    def calc_avg_realtime
      if @realtimes.empty?
        0
      else
        @realtimes.sum / @realtimes.length
      end
    end
  
    def calc_p95_realtime
      if @realtimes.empty?
        0
      else
        @realtimes.at(@realtimes.length * 0.95)
      end
    end
  
    def calc_p99_realtime
      if @realtimes.empty?
        0
      else
        @realtimes.at(@realtimes.length * 0.99)
      end
    end
  
    def count(metric)
      @num += 1
      if "Y" == metric["bsf"]
        @success_num += 1
      else
        @fail_num += 1
        @last_fail_trace = metric["tid"]
      end
      @realtimes.push(metric["rt"])
    end
  
    def calc_aggregate
      aggregate = ApiAggregate.new(@time, @service_name, @api)
      aggregate.num = @num
      aggregate.success_num = @success_num
      aggregate.fail_num = @fail_num
      aggregate.success_rate = calc_success_rate
      aggregate.fail_rate = calc_fail_rate
      aggregate.avg_realtime = calc_avg_realtime
      aggregate.p95_realtime = calc_p95_realtime
      aggregate.p99_realtime = calc_p99_realtime
      aggregate.last_fail_trace = @last_fail_trace
      aggregate
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
