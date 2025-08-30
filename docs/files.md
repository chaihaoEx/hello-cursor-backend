<!-- Generated: 2025-01-08 12:00:00 UTC -->

# 文件目录

Hello Quick 项目的完整文件组织结构和功能说明。

## 项目概述

Hello Quick 是一个 Spring Boot 3.5.5 项目，提供 REST API 服务。采用分层架构，集成 Lombok 简化开发。

## 核心源文件

### 主应用
- `src/main/java/com/kinkle/helloquick/HelloQuickApplication.java` - Spring Boot 主启动类

### Web 层
- `src/main/java/com/kinkle/helloquick/controller/HelloController.java` - REST API 控制器，包含 Lombok 示例

### 配置层
- `src/main/resources/application.properties` - 应用配置（服务器端口、日志级别）

## 测试文件

### 单元测试
- `src/test/java/com/kinkle/helloquick/HelloQuickApplicationTests.java` - Spring 上下文测试

## 构建系统

### Maven 配置
- `pom.xml` - 项目依赖、插件和构建配置

### 构建输出
- `target/classes/` - 编译后的类文件
- `target/hello-quick-0.0.1-SNAPSHOT.jar` - 可执行 JAR 包
- `target/surefire-reports/` - 测试报告

## 项目文档

### 说明文档
- `README.md` - 项目使用指南和快速开始
- `docs/project-overview.md` - 项目概述和特性说明
- `docs/architecture.md` - 系统架构和组件关系
- `docs/build-system.md` - 构建配置和流程
- `docs/testing.md` - 测试框架和运行指南
- `docs/development.md` - 开发规范和最佳实践
- `docs/deployment.md` - 打包部署和运维指南
- `docs/files.md` - 完整文件目录和功能说明

## 规则配置

### 全局规则
- `global-rules/` - 项目无关的通用规则
  - `github-issue-creation.mdc` - GitHub Issue 创建规范
  - `mcp-peekaboo-setup.mdc` - MCP Peekaboo 设置
  - `mcp-sync-rule.md` - MCP 同步规则
  - `setup-mcps.sh` - MCP 设置脚本

### 项目规则
- `project-rules/` - 项目特定的开发规则
  - `update-docs.mdc` - 文档更新规范
  - `java-check.mdc` - Java 代码检查规则
  - `spring-architect.mdc` - Spring 架构设计规范
  - `spring-patterns.mdc` - Spring 开发模式

## 依赖关系

**核心依赖**:
- Spring Boot Starter Web → Web 功能
- Spring Boot Starter Test → 测试框架
- Lombok → 代码生成

**配置文件依赖**:
- `pom.xml` → 所有依赖声明
- `application.properties` → 运行时配置

**源代码依赖**:
- `HelloQuickApplication.java` → 主类，依赖 Spring Boot
- `HelloController.java` → 控制器，依赖 Spring Web 和 Lombok

## 命名约定

**包结构**: `com.kinkle.helloquick.*` (按功能分包)

**类命名**: `*Controller`, `*Service`, `*Repository` (按职责命名)

**文件命名**: 与类名一致，`.java` 扩展名

**资源文件**: `application.properties`, `logback.xml` 等

## 构建依赖

**Maven**: 管理所有依赖和构建过程

**阿里云镜像**: 配置在 `pom.xml` 中，提升下载速度

**Java 17**: 编译和运行环境要求
