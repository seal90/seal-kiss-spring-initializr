
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
    @type apiaggregate
    tag hello_api_aggregate_tag
    origin HTTP_IN
  </filter>

  <filter hello_api_aggregate_tag>
    @type monitorexecutor
    tag monitor_executor
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
        compare_val 10a
      </api_rule>
    </rule>
  </filter>

  <match **>
    @type stdout
  </match>

</label>

## request test
curl --location '127.0.0.1:9880/hello-metric-digit.log' \
--header 'Content-Type: text/plain' \
--data '2024-09-12T08:52:09.148+08:00  INFO 1213 --- [seal-kiss-multi] [http-nio-8080-exec-1] [e6c0274fb41617164d57da2808516ef1-0cf7d392dd7325d9] MONITOR_DIGIT_LOG_LOGGER                 : {"o":"HTTP_IN","id":"DemoClientImpl.callBipartite()","rt":420,"bsf":"Y","ntf":"Y"}'