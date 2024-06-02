# 使用
执行下面命令可打包出文件
注意由于start-client使用了nodejs，若是调试，需要先执行下面命令将start-client编译
```shell
mvn -Dmaven.test.skip=true clean install
```

# 修改内容
## [Initializr.js](start-client%2Fsrc%2Fcomponents%2Freducer%2FInitializr.js)
修改 Package name = Group + Artifact.replaceAll('-','.')

## [application.yml](start-site%2Fsrc%2Fmain%2Fresources%2Fapplication.yml)
* Project 限定公司使用的 Maven
* Language 限定公司使用的 Java
* Project Metadata
  * Group Artifact Name 默认值
  * Packaging 限定 Jar
  * Java 限定 21
* 优化 Dependencies，减少公司内不用的，增加公司使用的。
* TODO 扩展自己的


## [StartInitializrMetadataUpdateStrategy.java](start-site%2Fsrc%2Fmain%2Fjava%2Fio%2Fspring%2Fstart%2Fsite%2Fsupport%2FStartInitializrMetadataUpdateStrategy.java)
* Spring Boot 限定统一使用 3.2.5
fetchSpringBootVersions Spring Boot 版本由获取服务器上的信息改为获取本地 [project_metadata.json](start-site%2Fsrc%2Fmain%2Fresources%2Fproject_metadata.json)

# 重要的类
* ProjectGenerationController