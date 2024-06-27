# seal-kiss-spring-initializr
目标： KISS = Keep It Simple & Stupid

* 快速构建团队通用项目实例
* 团队内优秀经验积累
* 利于理解业务的统一项目结构

# 项目
## 多模块项目模板类 [seal-kiss-multi-template](seal-kiss-multi-template)
构建统一项目模板，用于 [seal-kiss-initializr](seal-kiss-initializr) 项目自动生成。

## 快速构建初始化项目 [seal-kiss-initializr](seal-kiss-initializr)
部署此项目可以与 https://start.spring.io/ 一样快速初始化出自己的项目。
构建项目模板来自 [seal-kiss-multi-template](seal-kiss-multi-template)

## 经验积累 [seal-kiss-parent](seal-kiss-parent)
经验积累，以及功能增强，详见 [README](seal-kiss-parent/README.md)

## 网关及常用功能 [seal-kiss-gateway](seal-kiss-gateway)
基于spring cloud gateway 的网关项目，并增加常用功能。详见 [README](seal-kiss-gateway/README.MD)

## nginx [nginx](nginx)
与项目相配合使用时需要注意的配置。详见 [README](nginx/README.MD)