# -*- coding: utf-8 -*-

require "test-unit"
require "fluent/test"
require "fluent/test/driver/filter"
require "fluent/test/helpers"

require 'helper'

class MetricMonitorFilterTest < Test::Unit::TestCase
  include Fluent::Test::Helpers
  def setup
    Fluent::Test.setup
  end

  # default configuration for tests
  CONFIG = %[
    <rule>
      monitor_rule_id first_monitor_id
      metric_rule_id first_rule_id
      and_or and
      <metric_rule>
        minutes 1
        aggregation_type average
        compare_rule greater
        compare_val 0
      </metric_rule>
    </rule>
  ]

  def create_driver(conf = CONFIG)
    Fluent::Test::Driver::Filter.new(Fluent::Plugin::MetricMonitorFilter).configure(conf)
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
          [t, '{"time":"'+t_str+'","service_name":"seal-kiss-multi","code":"TEST_METRIC_1","sub_code":"TEST_METRIC_SUB_1","num":1,"metric_rule_id":"first_rule_id"}']
        ])
      end

      aggregate = d.instance.metric_aggregate
      assert_equal aggregate.size, 1
      rule_result = d.instance.rule_exec(time_now)
      assert_equal rule_result.size, 1

      rule_result = d.instance.rule_exec(time_now+60)
      assert_equal rule_result.size, 0
    end
  end
end
