
## fluent.conf

<source>
  @type http
  port 9880
  bind 0.0.0.0
  body_size_limit 32m
  keepalive_timeout 10s
  @label @hello
  <parse>
    @type none
  </parse>
</source>


<filter>
  @type stdout
</filter>

<label @hello>


  <filter *metric-digit.log>
    @type stdout
  </filter>

  <filter *metric-digit.log>
    @type metricaggregate
    tag hello_metric_aggregate_tag
    <metric>
      metric_rule_id first_rule_id
      service_name seal-kiss-multi
      code TEST_METRIC_1
      sub_code TEST_METRIC_SUB_1
    </metric>
  </filter>


  <filter hello_metric_aggregate_tag>
    @type metricmonitor
    tag hello_metric_monitor_tag
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
  </filter>

  <match **>
    @type stdout
  </match>

</label>



## request test
curl --location '127.0.0.1:9880/hello-metric-digit.log' \
--header 'Content-Type: text/plain' \
--data '2024-10-18T15:20:56.557+08:00  INFO 5772 --- [seal-kiss-multi] [http-nio-8080-exec-1] [557b69ef240e76aabc7639d2dfbd2bcd-d0bc7d0f2930b3a3] i.github.seal90.kiss.core.log.MetricLog  : {"code":"TEST_METRIC_1","subCode":"TEST_METRIC_SUB_1","num":1,"msg":"测试metric"}'