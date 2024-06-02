
# root
## [pom.xml](pom.xml)
* parent seal-kiss-parent
* artifactId 增加parent seal-kiss-multi-parent
* version 声明为 ${revision}
* packaging pom
* dependencies 更改为 seal-kiss-client seal-kiss-core
* TODO Dockerfile

## src
* 删除 src

# [seal-kiss-multi-client](seal-kiss-multi-client)
## [pom.xml](seal-kiss-multi-client%2Fpom.xml)
* parent 为 seal-kiss-multi-parent
* version ${revision}
* dependencies 改为 lombok jakarta.validation-api spring-web(provided)

## src
* 删除启动类
* 新增 类 [HelloWorldClient.java](seal-kiss-multi-client%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fclient%2FHelloWorldClient.java)
* 删除 [resources](seal-kiss-multi-client%2Fsrc%2Fmain%2Fresources)
* 删除测试文件夹 [test](seal-kiss-multi-client%2Fsrc%2Ftest)

# [seal-kiss-multi-start](seal-kiss-multi-start)
## [pom.xml](seal-kiss-multi-start%2Fpom.xml)
* parent 为 seal-kiss-multi-parent
* version ${revision}
* TODO 注册中心

## src
* TODO 启动类增加注册中心 [SealKissmultiStartApplication.java](seal-kiss-multi-start%2Fsrc%2Fmain%2Fjava%2Fio%2Fgithub%2Fseal90%2Fkiss%2Fmulti%2Fstart%2FSealKissmultiStartApplication.java)
* 配置参数使用 bootstrap [resources](seal-kiss-multi-start%2Fsrc%2Fmain%2Fresources)
* TODO 增加测试类 [test](seal-kiss-multi-start%2Fsrc%2Ftest)

# [seal-kiss-multi-integration](seal-kiss-multi-integration)
## [pom.xml](seal-kiss-multi-start%2Fpom.xml)
* parent 为 seal-kiss-multi-parent
* version ${revision}
* TODO mybatis https://mybatis.org/generator/running/runningWithMaven.html

# [seal-kiss-multi-common](seal-kiss-multi-common)
## [pom.xml](seal-kiss-multi-common%2Fpom.xml)
* parent 为 seal-kiss-multi-parent
* version ${revision}
* TODO 最小 dependency

## src 
* remove main func
* TODO log 
  * 分类型 
  * lazy to string

# [seal-kiss-multi-service](seal-kiss-multi-service)
## [pom.xml](seal-kiss-multi-service%2Fpom.xml)
* parent 为 seal-kiss-multi-parent
* version ${revision}
* TODO 最小 dependency

## src
* 删除 test resource

# [seal-kiss-multi-client-adapter](seal-kiss-multi-client-adapter)
## [pom.xml](seal-kiss-multi-client-adapter%2Fpom.xml)
* parent 为 seal-kiss-multi-parent
* version ${revision}
* TODO 最小 dependency

## src
