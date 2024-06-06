限流
https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/requestratelimiter-factory.html

# 接口相关
## 开放API

# 登陆相关
## 无须登陆白名单
## 登陆设备限制
## 登陆踢出
## 用户功能权限

# 灰度相关
## 灰度环境 - 查找标签
首先理解两个环境标识
* 当前服务运行的主环境 DAILY PRE PROD
* 当前服务要执行灰度的环境，可自定义，可没有（代表不执行灰度）
再思考当前服务要寻找执行服务的规则
* 优先使用同灰度标识的服务
* 再次使用主环境标识的服务


## 前端灰度 - 修改路由uri StaticGrayGatewayFilterFactory
假设url设计 protocol://hostname[:port]/static/{project}/**/*.*
协议://主机名[:端口]/固定static/{前端发布工程名}/任意级目录/任意文件
灰度实现
多级
protocol://hostname[:port]/static/{project}/{GRAY_FLAG}/**/*.*
一级
protocol://hostname[:port]/static/{GRAY_FLAG}/{any_file}