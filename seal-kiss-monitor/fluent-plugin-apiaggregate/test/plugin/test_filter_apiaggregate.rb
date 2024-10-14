# -*- coding: utf-8 -*-

require "test-unit"
require "fluent/test"
require "fluent/test/driver/filter"
require "fluent/test/helpers"

require 'helper'

class ApiAggregateFilterTest < Test::Unit::TestCase
  include Fluent::Test::Helpers
  def setup
    Fluent::Test.setup
  end

  # default configuration for tests
  CONFIG = %[
    origin HTTP_IN
  ]

  def create_driver(conf = CONFIG)
    Fluent::Test::Driver::Filter.new(Fluent::Plugin::ApiAggregateFilter).configure(conf)
  end

  sub_test_case 'tests for #filter' do
    test 'to test #filter' do
      d = create_driver
      d.instance.watch_start = false
      t = event_time("2016-06-10 19:46:32 +0900")
      d.run do
        d.feed('tag', [
          [t, {'message' => '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d1] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":420,"bsf":"Y","ntf":"Y"}'}],
          [t, {'message' => '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d2] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":430,"bsf":"N","ntf":"Y"}'}],
          [t, {'message' => '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d3] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":440,"bsf":"Y","ntf":"Y"}'}],
          [t, {'message' => '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d4] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":450,"bsf":"Y","ntf":"Y"}'}],
          [t, {'message' => '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d5] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":460,"bsf":"Y","ntf":"Y"}'}],
          [t, {'message' => '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d6] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.another()","rt":460,"bsf":"Y","ntf":"Y"}'}]
        ])
      end
      
      conter = d.instance.api_counter
      assert_equal conter.size, 2

      conter.each_value{|val|
        aggregate = val.calc_aggregate
        if  'DemoClientImpl.callBipartite()' == aggregate.api
          assert_equal aggregate.last_fail_trace, 'e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d2' 
        end
      }
    end
  end
end
