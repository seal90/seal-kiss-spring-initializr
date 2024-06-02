# 模块简介
```text
[seal-kiss-multi-client](seal-kiss-multi-client) 提供给其他服务的二方包
[seal-kiss-multi-client-adapter](seal-kiss-multi-client-adapter) 通过特定协议实现二方包能力的暴露，
[seal-kiss-multi-common](seal-kiss-multi-common) 基础能力通用包，例如日志打印
[seal-kiss-multi-integration](seal-kiss-multi-integration) 基础设施调用
[seal-kiss-multi-service](seal-kiss-multi-service) 业务能力实现
[seal-kiss-multi-start](seal-kiss-multi-start) 启动包，配置，测试类编写包
```

# 代码编写
## 定义对外接口(client)
* 参考 [DemoClient.java](seal-kiss-multi-client%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fclient%2Fdemo%2FDemoClient.java)
* 注意事项
  * 这个类是接口
  * 类上注解 @RequestMapping("/demo")
  * 请求方法在无特殊需求的情况下使用POST，可减少与使用者沟通事项
  * 方法注解 @PostMapping，尽量使用 @RequestBody， 不要使用 PathValue
  * 入参、返回值必须是自己定义的类型，若无具体类型返回值需返回 Result<Void>
  * 分页参考 dataPage
  * 参数校验
* 包下面不要引入其他依赖
* uri 的设计和接口名的设计要保障最大的相似，后面日志打印问题排查可更简单

## 实现对外接口(client-adapter)
* 参考 [DemoClientImpl.java](seal-kiss-multi-client-adapter%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fclient%2Fadapter%2Fhttp%2Fdemo%2FDemoClientImpl.java)
* 类上注解 @RestController，识别为 controller
* 方法上注解 @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true) 打印请求信息，由于异常信息在异常处理时打印，此处可以不用打印
* 方法参数校验 @Validated
* 参数转换使用 mapstruct，由于这里衔接 client 和 service 这里类型转换比较多。注意更新类后执行 maven clean 再次启动mapstruct生成方法才会变更。

## 业务规则定义及实现(service)
* 此处是实现业务逻辑的地方，根据业务复杂度规划好包结构，确保包结构的单向调用。
* 参考业务接口定义 [DemoService.java](seal-kiss-multi-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fservice%2Fdemo%2Fservice%2FDemoService.java)
* 参考业务接口实现 [DemoServiceImpl.java](seal-kiss-multi-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fservice%2Fdemo%2Fservice%2Fimpl%2FDemoServiceImpl.java)
* 将业务放在服务类中，而不是领域对象中，或者说选择贫血模型而不是充血模型。
* 调用层级
```text
    clientAdapter --> service --> manager --> node --> integration
                      业务层               原子/公共业务
    同级之间严禁相互调用，允许一定代码重复，相同业务可沉淀到下一级
    禁止下级调用上级
    可以跨级调用下级
      service 可以直接调用 node  integration
      manager 可以直接调用 integration
    业务扩展可以通过 Extension 来实现， service node 都可以调用扩展
```
* 由于同一个项目中业务是相对集中的，调用到的基础设施也会基本一致，建议将对基础设置的调用放置在最外侧即 [integration](seal-kiss-multi-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fservice%2Fintegration)

## 调用基础设施定义及实现(integration)
* 接口在service项目下 [DemoIntegration.java](seal-kiss-multi-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fservice%2Fintegration%2FDemoIntegration.java)
* 接口的实现在本项目中 [DemoIntegrationImpl.java](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Fimpl%2FDemoIntegrationImpl.java)
* 同理基础设施的调用在同一个项目中会公用，业务隔离不强所以放在一个包下面
* 根据基础设施类型不同分包
* 基础设施简单配置统一放在 [config](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Fconfig)
  * 例如通过 FeignConsumer 配置 [ClientConfig.java](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Fconfig%2FClientConfig.java)
  * 通过 FeignClient 配置 [DemoFeign.java](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Ffeign%2FDemoFeign.java)
* 定义在 service 项目，实现在 integration 项目，目的是通过依赖反转，使得 service 包不受 integration 中的二方、三方包定义的类影响
* 基础设施增强使用
  * mybatis 反向使用
    * 修改配置文件 [generatorConfig.xml](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fresources%2FgeneratorConfig.xml)
      * 注意修改 jdbcConnection table, 注意 domainObjectName 以 DO 结尾
    * 在 maven 中执行 mybatis-generator:generate
      * 参考 sql [db.sql](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fresources%2Fdb.sql)
      * 参考xml 文件 [DemoDAO.xml](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fresources%2Fmapper%2FDemoDAO.xml)
      * java 接口 [DemoDAO.java](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Fdb%2Fdao%2FDemoDAO.java)
      * java DO [DemoDO.java](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Fdb%2Fdos%2FDemoDO.java)
      * java 查询，注意此类非常好用 [DemoParam.java](seal-kiss-multi-integration%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fintegration%2Fdb%2Fdos%2FDemoParam.java)

## 通用(common)
* 常量值 [constant](seal-kiss-multi-common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fcommon%2Fconstant)
* 枚举值 [enums](seal-kiss-multi-common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fcommon%2Fenums)
  * 注意这里是业务内部使用的枚举，提供给二方包里面应该也有必要的枚举值，由于包隔离需要根据所需拷贝到二方包里面。不直接在client中是由于一枚举二方包里面的枚举数量，二枚举中的枚举值可能都不同，所以需要手动根据实际需要拷贝。
  * 错误枚举 [BizErrorReason.java](seal-kiss-multi-common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fcommon%2Fenums%2FBizErrorReason.java) 
    * OK ERROR SYS_* 被基础系统占用，请使用自己系统的名称做前缀命名错误类型。
* 日志打印 [log](seal-kiss-multi-common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fcommon%2Flog) 若core包 MetricLogScene NotifyLog 不能满足需求可根据实际情况编写
* 项目中常用工具类 [utils](seal-kiss-multi-common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fcommon%2Futils)

## 启动(start)
* 启动类在公司域名根目录下，引用公司的工具包会主动扫描加载起，在引用时要特别注意。
* 配置文件使用
  * 固定值，无须动态更新的配置直接放在配置文件中
  * 需要变动的放到对应环境配置文件或者配置中心
* 主环境
  * DAILY PRE PROD 默认 DAILY

# 初次使用注意
## 日志
* 查看配置文件配置文件是否满足需求 [logback-spring.xml](seal-kiss-multi-start%2Fsrc%2Fmain%2Fresources%2Flogback-spring.xml)
  * 默认异步、最长保存7天，每256M一个文件最大10G。
  * 期望将日志打印为JSON,修改参考 https://logback.qos.ch/manual/encoders.html
  * 注入 LazyToString 进行日志对象打印序列化
## 打包
* 修改 [pom.xml](pom.xml) revision 可以修改所有包的版本号
* 打包前需要先提交代码到git上，此时打包中含有git 信息

# 编码约定
## 限制api
* 业务代码不要使用 ThreadLocal,Aop

# 示例代码启动
## 数据库 sql
```sql
create database demo;
create table demo (
    id BIGINT AUTO_INCREMENT COMMENT '主键',
    string_attr VARCHAR(64) COMMENT '字符串测试',
    integer_attr INT COMMENT '整型测试',
    long_attr BIGINT COMMENT '大整型测试',
    big_decimal_attr DECIMAL(10,2) COMMENT '高精度测试',
    local_time_attr TIME COMMENT '时间测试',
    local_date_attr DATE COMMENT '日期测试',
    local_date_time_attr DATETIME COMMENT '日期时间测试',
    duration_attr BIGINT COMMENT '时长测试',
    PRIMARY KEY (id),
    KEY string_index (string_attr)
);
```
## 配置文件
* [application.yml](seal-kiss-multi-start%2Fsrc%2Fmain%2Fresources%2Fapplication.yml)

## 请求参数
* /demo/save
```json
{
    "stringAttr":"stringAttr",
    "integerAttr":1,
    "longAttr":2,
    "bigDecimalAttr":"2.13",
    "localTimeAttr":"11:11:11",
    "localDateAttr":"2022-12-12",
    "localDateTimeAttr":"2023-12-12T12:12:12",
    "durationAttr":"PT10S"
}
```

# TODO
* mapstract 在修改类后，不自动更新
* 扩展点使用
* 制作template脚本 [build_template.sh](build_template.sh)
* 当前操作人约定
* 消息消费者的注册
  * integration 调用 service
* 环境使用
  * 多环境
  * 子环境
* DOCKER 打包
* 对外能力的暴露
  * 对外暴露协议 http、mq、task
  * 在这个角度讲 client-adapter 应该放到 integration 里面去
* 注册中心&配置中心