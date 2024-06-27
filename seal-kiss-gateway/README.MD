# 限流
spring 官方提供了 RequestRateLimiterGatewayFilterFactory 来实现限流 https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/requestratelimiter-factory.html
<br/> RequestRateLimiterGatewayFilterFactory 是作用在路由上的，同一个路由中出现例如秒杀接口，其流量会超过其他接口的大小限制很多，可以选择将其接口独立出一个路由来实现，或者使用 [RateLimitPathFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FRateLimitPathFilter.java)
<br/> 目前 [RateLimitPathFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FRateLimitPathFilter.java) 使用的是单节点限流，若网关数量基本固定不变，nginx 采用轮询方式时，则可以使用 限流/节点数 来配置限流量，也可以改为使用 redis 来实现，redis 方式已注释在源代码中

# 登陆相关
## 路径操作相关 [LimitPathFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FLimitPathFilter.java)
### 开放API
所有开放出去的接口必须在网关定义，否则会返回 404（注意接口地址不要出现PathValue，httpmethod 限制GET POST 建议仅采用POST）
### 无须登陆白名单
若代理的是静态资源，可以直接将整个路由设置为白名单，或者对路由下某些具体的路径配置白名单
### 用户功能权限
校验用户访问功能权限，服务获取路由下请求方法+路径与权限对应关系；在用户登陆时，获取当前人的权限信息，并转为bigmap；在访问某接口时，根据访问路由下的方法和路径获取权限值，根据权限值判断此用户是否有权限

## 登陆 [LoginController.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Flogin%2Fcontroller%2FLoginController.java)
访问 @PostMapping("/login") 完成登陆
## 登陆踢出（未实现）
通过在登陆时将信息存储到缓存 [AuthCache.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Flogin%2Fservice%2Fimpl%2FAuthCache.java)
在请求验证权限时验证权限 [AuthLimitPathFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FAuthLimitPathFilter.java)
还欠缺一个将用户信息传递给下游服务的filter, 注意传递给下游信息的header头信息一定要主动清空下端上传递的值。
## 登陆设备限制（未实现）

# 灰度相关
## 服务灰度 
### 灰度规则计算 [GrayCalculateFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FGrayCalculateFilter.java)
灰度标识可能来自两方面，一 前端传递，二根据标识计算，标识包括登陆人，前端传递用于计算的值等。
<br/> 使用 seal.kiss.gray.subSetEnvRequestKey:SUB_SET_ENV 头传递

### 灰度规则执行 - 多主环境灰度
两个环境标识：主环境标识，用于区分多个不同主环境；子环境标识，用于本主环境下灰度服务选择。
<br/> 在注册服务时，两个标识默认都为空，需要配置 seal.kiss.env.main seal.kiss.env.subSet 环境变量
<br/> 在调用时，主环境标识，默认 DAILY； 子环境标识，默认无
<br/> 在选择实例时，选择同主环境，优先选择同灰度环境，再选择同主环境同名的子环境

## 前端灰度 - 修改路由uri [GrayPathFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FGrayPathFilter.java)
假设url设计 protocol://hostname[:port]/static/{project}/\*\*/\*.\*
<br/> 协议://主机名[:端口]/固定static/{前端发布工程名}/任意级目录/任意文件
<br/> 灰度实现
<br/> 多级
<br/> protocol://hostname[:port]/static/{project}/{GRAY_FLAG}/**/*.*
<br/> 一级
<br/> protocol://hostname[:port]/static/{GRAY_FLAG}/{any_file}

# 类
DiscoveryClientServiceInstanceListSupplier
<br/> LoadBalancerClientConfiguration