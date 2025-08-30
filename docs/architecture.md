<!-- Generated: 2025-01-08 12:00:00 UTC -->

# 架构文档

Hello Quick 采用经典的分层架构模式，基于 Spring Boot 框架构建。

## 系统组织

**分层结构**:
- **Controller 层**: REST API 接口 (`src/main/java/com/kinkle/helloquick/controller/`)
- **Service 层**: 业务逻辑处理 (待扩展)
- **Repository 层**: 数据访问层 (待扩展)
- **Configuration**: 应用配置 (`src/main/resources/`)

## 组件映射

**核心组件**:
- **主应用类**: `src/main/java/com/kinkle/helloquick/HelloQuickApplication.java` (Spring Boot 启动入口)
- **Web 控制器**: `src/main/java/com/kinkle/helloquick/controller/HelloController.java` (REST API 端点)
- **应用配置**: `src/main/resources/application.properties` (服务器和日志配置)

## 关键文件

**入口点**: `HelloQuickApplication.java` lines 6-11 (Spring Boot 主类，包含 `@SpringBootApplication`)

**API 接口**: `HelloController.java` lines 13-20 (GET `/api/hello` 端点，包含 Lombok 使用示例)

**配置管理**: `application.properties` lines 1-10 (服务器端口、应用名称、日志级别配置)

## 数据流

1. **请求入口**: HTTP 请求 → `HelloController.sayHello()` (line 16)
2. **日志记录**: 使用 SLF4J 记录请求 (line 17)
3. **响应构建**: 创建 `HelloResponse` 对象 (line 18)
4. **JSON 返回**: Spring Boot 自动序列化为 JSON 响应

**关键代码示例**:
```java
// From src/main/java/com/kinkle/helloquick/controller/HelloController.java:13-21
@GetMapping
public HelloResponse sayHello() {
    log.info("Hello endpoint called");
    return new HelloResponse("Hello, Spring Boot 3.5.5 with Lombok!");
}
```

## 依赖关系

**Spring Boot Starter**: 自动配置 Web 功能和依赖注入
**Lombok**: 编译时生成 getter/setter/构造函数，简化 POJO 类
**Maven**: 管理项目依赖和构建生命周期
