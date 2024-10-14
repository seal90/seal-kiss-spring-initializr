# -*- coding: utf-8 -*-
require "test-unit"
require "fluent/test"
require "fluent/test/driver/filter"
require "fluent/test/helpers"

require 'helper'

class MonitorExecutorFilterTest < Test::Unit::TestCase
  include Fluent::Test::Helpers
  def setup
    Fluent::Test.setup
  end

  # default configuration for tests
  CONFIG = %[
    tag hello_api_aggregate_tag
    <rule>
      rule_id RULE_ID_1
      service_name seal-kiss-multi
      and_or and
      <api_rule>
        minutes 1
        api DemoClientImpl.callBipartite()
        metric_type avg_realtime
        aggregation_type average
        compare_rule greater
        compare_val 10
      </api_rule>
    </rule>
  ]

  def create_driver(conf = CONFIG)
    Fluent::Test::Driver::Filter.new(Fluent::Plugin::MonitorExecutorFilter).configure(conf)
  end

  sub_test_case 'tests for #filter' do
    test 'to test #filter' do
      time_now = Time.new(Time.now.strftime('%Y-%m-%d %H:%M:00 +0000'))
      d = create_driver
      d.instance.watch_start = false
      # t = event_time("2016-06-10 19:46:32 +0900")
      t = time_now
      t_str = time_now.strftime('%Y-%m-%d %H:%M:00 +0000')
      d.run do
        d.feed('tag', [
          [t, '{"time":"'+t_str+'","service_name":"seal-kiss-multi","api":"DemoClientImpl.callBipartite()","num":1,"success_num":1,"fail_num":0,"success_rate":1.0,"fail_rate":0.0,"avg_realtime":420,"p95_realtime":420,"p99_realtime":420,"last_fail_trace":""}'],
          [t, '{"time":"'+t_str+'","service_name":"seal-kiss-multi","api":"DemoClientImpl.callBipartite()","num":1,"success_num":1,"fail_num":0,"success_rate":1.0,"fail_rate":0.0,"avg_realtime":420,"p95_realtime":420,"p99_realtime":420,"last_fail_trace":"1212"}']
        ])
      end

      aggregate = d.instance.api_aggregate
      assert_equal aggregate.size, 1
      rule_result = d.instance.rule_exec(time_now)
      assert_equal rule_result.size, 1

      rule_result = d.instance.rule_exec(time_now+60)
      assert_equal rule_result.size, 0
    end
  end

end
