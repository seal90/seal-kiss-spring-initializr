# 限流
https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/requestratelimiter-factory.html

# 登陆相关
## 登陆设备限制
## 登陆踢出

## 路径操作相关 [LimitPathFilter.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fgateway%2Ffilter%2FLimitPathFilter.java)
### 开放API
所有开放出去的接口必须在网关定义，否则会返回 404（注意接口地址不要出现PathValue）
### 无须登陆白名单
### 用户功能权限

# 灰度相关
## 服务灰度 - 查找标签
增加灰度能力，同环境下多子环境的实现
* 首先理解两个环境标识，在各个环境由环境变量提供，业务应用无须配置
  * 当前服务运行的主环境(${seal.kiss.env.run:DAILY}) DAILY PRE PROD (seal.kiss.env.gray)
  * 当前服务要执行灰度的环境(request.header\[SEAL-GRAY-ENV] or ${seal.kiss.env.gray:})，可自定义，可没有（代表不执行灰度，同时代表不可被其他服务调用到）,需求涉及多个服务变更可以设置为共同的灰度标识，则可在线下调试
    * 网关在 request.header\[SEAL-GRAY-ENV] 取值
    * 服务在 ${seal.kiss.env.gray:} 取值
* 再思考当前服务要寻找执行服务的规则
  * 当前灰度标识不为空，则优先使用同灰度标识的服务
  * 再次使用主环境标识的服务


## 前端灰度 - 修改路由uri StaticGrayGatewayFilterFactory
假设url设计 protocol://hostname[:port]/static/{project}/**/*.*
协议://主机名[:端口]/固定static/{前端发布工程名}/任意级目录/任意文件
灰度实现
多级
protocol://hostname[:port]/static/{project}/{GRAY_FLAG}/**/*.*
一级
protocol://hostname[:port]/static/{GRAY_FLAG}/{any_file}

# 类
DiscoveryClientServiceInstanceListSupplier
LoadBalancerClientConfiguration