# -*- coding: utf-8 -*-

require "test-unit"
require "fluent/test"
require "fluent/test/driver/filter"
require "fluent/test/helpers"

require 'helper'

class MetricAggregateFilterTest < Test::Unit::TestCase
  include Fluent::Test::Helpers
  def setup
    Fluent::Test.setup
  end

  # default configuration for tests
  CONFIG = %[
    <metric>
      metric_rule_id first_rule_id
      service_name seal-kiss-multi
      code TEST_METRIC_1
      sub_code TEST_METRIC_SUB_1
    </metric>
  ]

  def create_driver(conf = CONFIG)
    Fluent::Test::Driver::Filter.new(Fluent::Plugin::MetricAggregateFilter).configure(conf)
  end

  sub_test_case 'tests for #filter' do
    test 'to test #filter' do
      d = create_driver
      d.instance.watch_start = false
      t = event_time("2016-06-10 19:46:32 +0900")
      d.run do
        d.feed('tag', [
          [t, {'message' => '2024-10-18T15:20:56.557+08:00  INFO 5772 --- [seal-kiss-multi] [http-nio-8080-exec-1] [557b69ef240e76aabc7639d2dfbd2bcd-d0bc7d0f2930b3a3] i.github.seal90.kiss.core.log.MetricLog  : {"code":"TEST_METRIC_1","subCode":"TEST_METRIC_SUB_1","num":1,"msg":"测试metric"} '}],
          [t, {'message' => '2024-10-18T15:20:56.557+08:00  INFO 5772 --- [seal-kiss-multi] [http-nio-8080-exec-1] [557b69ef240e76aabc7639d2dfbd2bcd-d0bc7d0f2930b3a3] i.github.seal90.kiss.core.log.MetricLog  : {"code":"TEST_METRIC_1","subCode":"TEST_METRIC_SUB_1","num":1,"msg":"测试metric"} '}]
        ])
      end
      counter = d.instance.metric_counter
      val = counter["seal-kiss-multi_TEST_METRIC_1_TEST_METRIC_SUB_1"]
      assert_equal val.num, 2
    end
  end
end
