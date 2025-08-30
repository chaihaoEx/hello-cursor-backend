<!-- Generated: 2025-01-08 12:00:00 UTC -->

# Hello Quick

一个基于 Spring Boot 3.5.5 的快速启动项目，提供 REST API 服务。核心文件：`src/main/java/com/kinkle/helloquick/HelloQuickApplication.java`

## 快速开始

**编译运行**:
```bash
mvn spring-boot:run  # 直接运行
```

**构建打包**:
```bash
mvn clean package   # 打包为 JAR
java -jar target/hello-quick-0.0.1-SNAPSHOT.jar
```

**API 访问**: http://localhost:8080/api/hello

## 核心文件

- **主类**: `src/main/java/com/kinkle/helloquick/HelloQuickApplication.java`
- **配置**: `pom.xml` (Maven), `src/main/resources/application.properties` (应用)
- **控制器**: `src/main/java/com/kinkle/helloquick/controller/HelloController.java`

## 技术栈

Java 17 + Spring Boot 3.5.5 + Lombok，已配置阿里云 Maven 镜像

## 详细文档

- **[项目概述](docs/project-overview.md)** - 功能特性和技术栈
- **[架构设计](docs/architecture.md)** - 系统结构和组件关系
- **[构建系统](docs/build-system.md)** - Maven 配置和构建流程
- **[测试指南](docs/testing.md)** - 单元测试和集成测试
- **[开发规范](docs/development.md)** - 代码规范和开发模式
- **[部署运维](docs/deployment.md)** - 打包部署和监控配置
- **[文件目录](docs/files.md)** - 完整文件结构说明
