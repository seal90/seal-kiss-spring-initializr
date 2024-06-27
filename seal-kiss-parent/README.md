# seal-kiss-parent
通用、常见的友好的工具定义及汇总

# 关键点
## jdk 21
* java 向下兼容；
* jdk21 G1 成为默认垃圾回收器，默认意味着在此版本已经成熟；
* 支持虚拟线程；

## 版本管理
使用 flatten-maven-plugin 对项目版本统一管理，修改 parent pom \<revision>0.0.1-SNAPSHOT\</revision> 可对所有模块版本生效

## 添加打包 git 信息
使用 git-commit-id-maven-plugin 将git提交信息放到打的包里面，对于长期不维护的项目，可根据包里面的git信息查看正在运行代码版本。注意第一次打包时需先执行git commit操作，否则会阻止打包。

## loombok
使用 loombok 可快速生成 get set 方法。

## mapstruct
使用 mapstruct 可以快速编写出类型转换（beanutils 使用反射，mapstruct生成java类），为方便写单元测试，这里不要和spring配合使用。

相关文档 https://mapstruct.org/documentation/dev/reference/html/

注意：目前有个问题，修改类之后需要执行maven clean 之后属性映射才可以生成

## 多 model 项目
初次接触多 model 项目会有很大不适应，我想也会成为许多人放弃使用的原因，适应了解之后会发现多 model 的是非常合理好用的。

了解每个 model 的作用可以快速入手多 model 项目。总体思路是在按照领域拆分的系统中，每个系统需要完成被分配的领域业务，
以特定的协议（例如http, grpc）对外提供业务能力，通常也需要依赖基础设施（例如db, cache）提供的能力，以及集群内其他服务提供的二方能力。

在代码中实现时的模块如下
```text
client
    * 定义服务提供能力的接口及出入参
    * 提供业务中的枚举状态，注意出入参不要使用枚举
    * 使用拥有业务含义且易理解的字符串代替数值做枚举状态值
client-adapter
    * 通过特定协议暴露接口给调用者
    * 链接接口与服务
service
    * 领域服务业务的实现
    * 依赖二方/中间件能力声明
integration
    * 依赖二方/中间件能力实现
common
    * 通用业务能力
start
    * 启动
    * 配置

```
实现时的包依赖
* client

引入 jakarta.validation-api，由于 scope=provided 所以在二方使用者使用时，不引入校验则也可以正常运行 ,由于 spring-boot-starter-validation 会引入整个spring-boot, 避免错误引入所以这里未使用
```xml
		<dependency>
			<groupId>jakarta.validation</groupId>
			<artifactId>jakarta.validation-api</artifactId>
			<scope>provided</scope>
		</dependency>

        <!--		<dependency>-->
        <!--			<groupId>org.springframework.boot</groupId>-->
        <!--			<artifactId>spring-boot-starter-validation</artifactId>-->
        <!--			<scope>provided</scope>-->
        <!--		</dependency>-->
```
引入 spring-web 增加请求路径类
```xml
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-web</artifactId>
			<scope>provided</scope>
		</dependency>
```


## 扩展的能力
### 系统间交互基础信息类 [seal-kiss-base](seal-kiss-base)
与前端、调用者约定返回的基础信息，例如 Result, Page 相关

### 核心/基础能力包 [seal-kiss-core](seal-kiss-core)
业务中终止执行的业务异常: [ServiceException.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Fexception%2FServiceException.java)，注意 [ErrorReason.java](seal-kiss-base%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fbase%2Fexception%2FErrorReason.java)

扩展点: 一个业务需要多个实现时，通常会考虑模板，由于每个实现都有自己的特殊地方导致模板很难设计通用，此时可以考虑扩展点，对于业务的某个点进行扩展。

日志：
  * 延迟序列化：[LazyToString.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FLazyToString.java) 在打印日志时，通常需要Json序列化一下对象，若打印的级别高于要输出的级别会导致一次无效的序列化执行，可以使用延迟序列化来避免
  * 业务统计指标 [MetricLog.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FMetricLog.java) 例如要统计订单状态变化可以使用此日志打印输出
  * 监控指标 [MonitorLog.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FMonitorLog.java) 统计接口出入参以及耗时信息
  * 通知指标 [NotifyLog.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FNotifyLog.java) 当出现特定场景需要程序通知出现非预期的场景时使用

常用的参数校验: [AssertUtils.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Futils%2FAssertUtils.java)

常用请求信息获取 [RequestUtils.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Futils%2FRequestUtils.java)

### feign 扩展 [seal-kiss-feign-plugin](seal-kiss-feign-plugin)
配合程序提供的二方包能力，将二方包接口注册为实例

实现与 spring 提供的能力对应
* 开启 @EnableFeignClients [EnableFeignConsumers.java](seal-kiss-feign-plugin%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Ffeign%2Fplugin%2FEnableFeignConsumers.java)
* 配置 @FeignClient [FeignConsumer.java](seal-kiss-feign-plugin%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Ffeign%2Fplugin%2FFeignConsumer.java)

实现中当前版本 判断 handler，无须修改 RequestMappingHandlerMapping
```java
/**
 * {@inheritDoc}
 * <p>Expects a handler to have a type-level @{@link Controller} annotation.
 */
@Override
protected boolean isHandler(Class<?> beanType) {
  return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
}
```

Feign 不允许注册 @RequestMapping 修改 SpringMvcContract
```java
		@Override
		protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
			RequestMapping classAnnotation = findMergedAnnotation(clz, RequestMapping.class);
			FeignClient feignClassAnnotation = findMergedAnnotation(clz, FeignClient.class);
			if (classAnnotation != null && feignClassAnnotation != null) {
				LOG.error("Cannot process class: " + clz.getName()
						+ ". @RequestMapping annotation is not allowed on @FeignClient interfaces.");
				throw new IllegalArgumentException("@RequestMapping annotation not allowed on @FeignClient interfaces");
			}

			CollectionFormat collectionFormat = findMergedAnnotation(clz, CollectionFormat.class);
			if (collectionFormat != null) {
				data.template().collectionFormat(collectionFormat.value());
			}
		}
```

### [seal-kiss-spring-cloud-extension](seal-kiss-spring-cloud-extension) spring cloud 的能力延伸
TODO 考虑将feign 挪到此包下
#### loadbalancer 的扩展 [loadbalancer](seal-kiss-spring-cloud-extension%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fspring%2Fcloud%2Fextension%2Floadbalancer)
[MultiMainZoneServiceInstanceListSupplier.java](seal-kiss-spring-cloud-extension%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fspring%2Fcloud%2Fextension%2Floadbalancer%2FMultiMainZoneServiceInstanceListSupplier.java)

两个环境标识：主环境标识，用于区分多个不同主环境；子环境标识，用于本主环境下灰度服务选择。

在注册服务时，两个标识默认都为空，需要配置 seal.kiss.env.main seal.kiss.env.subSet 环境变量

在调用时，主环境标识，默认 DAILY； 子环境标识，默认无

在服务调用时，选择同主环境，优先选择同灰度环境，再选择同主环境同名的子环境

#### 配置文件灰度
优先使用灰度名称的，再选择使用服务名称的配置文件
```yaml
spring:
  config:
    import:
      - optional:nacos:${spring.application.name}.yml
      - optional:nacos:${spring.application.name}-${seal.kiss.env.subSet:}.yml
```

### 日志详述
* 注意事项
  * 由于异常日志打印堆栈信息太多，默认到日志文件中的为 Ex{short} 可修改为 Ex https://logback.qos.ch/manual/layouts.html#xThrowable
  * 由于使用json打印日志太耗费存储，默认未使用JSON打印 修改参考 https://logback.qos.ch/manual/encoders.html
* 为避免日志级别不打印，但调用 json.toString 带来资源消耗，可使用 [LazyToString.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FLazyToString.java)
* 业务指标统计 [MetricLog.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FMetricLog.java)
* 通知日志 [NotifyLog.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FNotifyLog.java)
* 方法监控 [MonitorLog.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Flog%2FMonitorLog.java)
* 日志分文件
  * APP_LOG_PATH ${user.home}/${APPLICATION_NAME}/logs 
  * 通常日志输出 ${APP_LOG_PATH}/${APPLICATION_NAME}-common.log.%d{yyyy-MM-dd}.%i}
  * 业务指标统计 ${APP_LOG_PATH}/${APPLICATION_NAME}-metric-digit.log.%d{yyyy-MM-dd}.%i}
  * 通知日志 ${APP_LOG_PATH}/${APPLICATION_NAME}-notify-digit.log.%d{yyyy-MM-dd}.%i}
  * 方法监控日志 ${APP_LOG_PATH}/${APPLICATION_NAME}-monitor-digit.log.%d{yyyy-MM-dd}.%i}

## 开发中的建议事项
###  数据库事务模板
* 不建议使用 @Transactional
* 推荐使用下面编程方式，不要嵌套事务
```java
// 有返回值
transactionTemplate.execute((status) -> {

    return null;
});

// 无返回值
transactionTemplate.execute(new TransactionCallbackWithoutResult() {

    @Override
    protected void doInTransactionWithoutResult(TransactionStatus status) {

    }
});
```

### 时间处理
* 使用java8时间类型，使用默认IOS协议传输
```json
{
    "stringAttr":"stringAttr",
    "integerAttr":3,
    "longAttr":4,
    "bigDecimalAttr":"4.13",
    "localTimeAttr":"14:11:11",
    "localDateAttr":"2024-12-12",
    "localDateTimeAttr":"2024-12-12T12:12:12",
    "durationAttr":"PT14S"
}
```

### 数据库配置
* mybatis 配置
  * mybatis 配置参数 https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/
  * 默认使用了 HikariCP 数据库连接池，其配置 spring.datasource.hikari.* 配置类为 DataSourceProperties

### 安全
* 当前用户获取
  * by header
* 接口权限
  * by gateway

# spring 实用接口
* DispatcherServlet#doService
* HandlerInterceptor
* HandlerMethodArgumentResolver
* RequestBodyAdvice
* ResponseBodyAdvice
* AnnotatedElementUtils#findMergedAnnotation

# TODO
* 优化 Feign 注册代码