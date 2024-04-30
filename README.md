# seal-kiss-spring-initializr
KISS = Keep It Simple & Stupid

> 每次构建新项目，都需要从原项目中去汲取精华借鉴到新项目中，但是每次不能一次把所有优秀的经验复制过来，需回忆并拷贝，比较麻烦，遂构建此项目，将优秀的经验保留下来，若使用不到则可以删掉，做减法要比再次回忆做加法容易的多。

> 这里用词“优秀的经验”而不是好的，因为很难定义什么是好，优秀通常能达成一致。

> 优秀会意味着会编写更少的代码？不，可能会更多，为了保持单一职责，使用多model模式强制将数据模型按照职责拆分，代码量会上升，使用技术可以降低一些，但与使用单类从请求到入库是多很多的。

> 优秀意味着合理吗？是的，按照合理的方案解决问题才是优秀的。

> 优秀意味着简单吗？简单很难界定，我曾经也写过一个类从接收请求到入库，到接触多model时有一段时间也是感觉复杂度上升，但理解之后就会感觉合理，因为合理才会感觉简单。

> 此项目中的做法都是优秀的吗？项目中的做法都是作者认为优秀的，若有更好做法，请给作者提issue,升级或者补充到项目中。

# 生产中要求
* 二方调用
* 日志
  * 问题排查
  * 接口服务指标
  * 业务指标
  * 主动告警
* 数据库访问

# 部署需要更改的配置项目

# 模块介绍

# 关键点
## jdk 21
> java 向下兼容；G1 成为默认垃圾回收器，默认意味着在此版本已经成熟；支持虚拟线程；

## 版本管理
> 使用 flatten-maven-plugin 对项目版本统一管理，修改 <revision>0.0.1-SNAPSHOT</revision> 可对所有模块版本生效

## 添加打包 git 信息
> 使用 git-commit-id-maven-plugin 将git提交信息放到打的包里面，对于长期不维护的项目，可根据包里面的git信息查看正在运行代码版本。

## 多 model 项目
> 初次接触多 model 项目会有很大不适应，也会成为许多人放弃使用的原因，适应了解之后会发现多 model 的是非常合理好用的。

> 了解每个 model 的作用可以快速入手多 model 项目。每个项目需要完成被分配的领域业务，以特定的协议对外提供业务能力，通常也需要依赖基础设施提供的能力。

> 在代码实现时的模块要比模型多
```text
TODO 代码结构
```

## 扩展的能力
### [seal-kiss-client](seal-kiss-client)
与前端、调用者约定返回的基础信息，例如 Result, Page 相关

### [seal-kiss-core](seal-kiss-core)
业务中终止执行的业务异常: [ServiceException.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Fexception%2FServiceException.java)
扩展点: 一个业务需要多个实现时，通常会考虑模板，由于每个实现都有自己的特殊地方导致模板很难设计通用，此时可以考虑扩展点，对于业务的某个点进行扩展。
常用的参数校验: [AssertUtils.java](seal-kiss-core%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fcore%2Futils%2FAssertUtils.java)

