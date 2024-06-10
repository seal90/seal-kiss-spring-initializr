# seal-kiss-parent
通用、常见的好的工具定义及汇总

# 关键点
## jdk 21
* java 向下兼容；
* jdk21 G1 成为默认垃圾回收器，默认意味着在此版本已经成熟；
* 支持虚拟线程；

## 版本管理
使用 flatten-maven-plugin 对项目版本统一管理，修改 parent pom \<revision>0.0.1-SNAPSHOT\</revision> 可对所有模块版本生效

## 添加打包 git 信息
使用 git-commit-id-maven-plugin 将git提交信息放到打的包里面，对于长期不维护的项目，可根据包里面的git信息查看正在运行代码版本。

## loombok
使用 loombok 可快速生成get set 方法。

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
    * 领域服务能力的实现
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

引入 jakarta.validation-api，由于 scope=provided 所以在使用者不引入校验则也可以正常运行 ,由于 spring-boot-starter-validation 会引入整个spring-boot, 避免错误引入所以这里未使用
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
引入 spring-web 请求路径
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
		CollectionFormat collectionFormat = findMergedAnnotation(clz, CollectionFormat.class);
		if (collectionFormat != null) {
			feign.CollectionFormat value = collectionFormat.value();

			data.template().collectionFormat(collectionFormat.value());
		}
	}
```

### [seal-kiss-spring-cloud-extension](seal-kiss-spring-cloud-extension) spring cloud 的能力延伸
TODO 考虑将feign 挪到此包下
#### loadbalancer 的扩展 [loadbalancer](seal-kiss-spring-cloud-extension%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fspring%2Fcloud%2Fextension%2Floadbalancer)
增加灰度能力，同环境下多子环境的实现
* 首先理解两个环境标识，在各个环境由环境变量提供，业务应用无须配置
  * 当前服务运行的主环境(${seal.kiss.env.run:DAILY}) DAILY PRE PROD (seal.kiss.env.gray)
  * 当前服务要执行灰度的环境(request.header\[SEAL-GRAY-ENV] or ${seal.kiss.env.gray:})，可自定义，可没有（代表不执行灰度，同时代表不可被其他服务调用到）,需求涉及多个服务变更可以设置为共同的灰度标识，则可在线下调试
    * 网关在 request.header\[SEAL-GRAY-ENV] 取值
    * 服务在 ${seal.kiss.env.gray:} 取值
* 再思考当前服务要寻找执行服务的规则
  * 当前灰度标识不为空，则优先使用同灰度标识的服务
  * 再次使用主环境标识的服务

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

### 线程池的选择
* 执行超时时间结束，线程结束
```java

    /**
     * invokeAll(tasks, 2, TimeUnit.SECONDS);
     * 执行2s之内的成功获取到结果，超时的的到 InterruptedException，获取结果时 CancellationException
     */
    @Test
    public void test3() {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // 创建任务列表
        List<Callable<String>> tasks = List.of(
                () -> {
                    try {
                        System.out.println("1开始执行");
                        Thread.sleep(1000); // 模拟耗时操作
                        System.out.println("1结束执行");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("1Exception 结束");
                    }
                    return "Task 1 completed";
                },
                () -> {
                    try {
                        System.out.println("2开始执行");
                        Thread.sleep(3000); // 模拟耗时操作
                        System.out.println("2结束执行");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("2Exception 结束");
                    }
                    return "Task 2 completed";
                },
                () -> {
                    try {
                        System.out.println("3开始执行");
                        Thread.sleep(1000); // 模拟耗时操作
                        System.out.println("3结束执行");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("3Exception 结束");
                    }
                    return "Task 3 completed";
                }
        );

        try {
            // 执行任务并设置超时时间为2秒
            List<Future<String>> futures = executor.invokeAll(tasks, 2, TimeUnit.SECONDS);

            // 遍历任务结果
            for (Future<String> future : futures) {
                System.out.println("开始获取结果");
                try {
                    String result = future.get();
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("获取结果异常");
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
        } finally {
            // 关闭线程池
//            executor.shutdown();
        }

        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
        }
    }
```
* 执行超时时间结束，线程继续
```java
    /**
     * get(3, TimeUnit.SECONDS); 
     * get 3s 超时后，执行线程依旧执行完成了任务
     * 即打印出 打印结束
     */    
    @Test
    public void test1() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(50);
        threadPoolTaskExecutor.setMaxPoolSize(50);
        threadPoolTaskExecutor.setRejectedExecutionHandler((r,executor)-> {
            throw new RejectedExecutionException("Failed to start a new thread");
        });
        threadPoolTaskExecutor.setQueueCapacity(500);
        threadPoolTaskExecutor.setThreadNamePrefix("MINE-POOL-");
        threadPoolTaskExecutor.initialize();

        Future<String> future = threadPoolTaskExecutor.submit(()->{
            try {
                System.out.println("打印开始");
                Thread.sleep(10000L);
                System.out.println("打印结束");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hello";
        });

        String val = null;
        try {
            val = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        System.out.println(val);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
        }

    }
```

### 安全
* 当然用户获取
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