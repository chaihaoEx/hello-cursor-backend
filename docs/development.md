<!-- Generated: 2025-01-08 12:00:00 UTC -->

# 开发文档

Spring Boot 开发指南，包含代码规范、常用模式和开发工作流。

## 开发环境

**IDE**: IntelliJ IDEA、Eclipse 或 VS Code (推荐 Spring Boot 插件)

**JDK**: Java 17+ (项目配置在 `pom.xml` lines 23-25)

**构建工具**: Maven 3.6+

**版本控制**: Git (推荐)

## 代码规范

### 包结构
```
com.kinkle.helloquick
├── controller/     # REST 控制器
├── service/        # 业务逻辑层
├── repository/     # 数据访问层
├── config/         # 配置类
└── dto/           # 数据传输对象
```

### 类命名
- **控制器**: `*Controller` (如 `HelloController`)
- **服务**: `*Service` (如 `UserService`)
- **仓库**: `*Repository` (如 `UserRepository`)
- **配置**: `*Config` (如 `SecurityConfig`)

## 常用模式

### Lombok 使用模式

**数据类**:
```java
// From src/main/java/com/kinkle/helloquick/controller/HelloController.java:22-25
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class HelloResponse {
    private String message;
}
```

**日志记录**:
```java
// From src/main/java/com/kinkle/helloquick/controller/HelloController.java:11
@Slf4j
public class HelloController {
    // 自动注入 log 字段
}
```

### REST API 模式

**基础控制器**:
```java
@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getById(@PathVariable Long id) {
        // 业务逻辑
    }
    
    @PostMapping
    public ResponseEntity<Resource> create(@RequestBody CreateResourceRequest request) {
        // 创建逻辑
    }
}
```

### 配置管理

**应用配置**: `src/main/resources/application.properties`

**环境配置**: `application-dev.properties`, `application-prod.properties`

## 开发工作流

### 添加新功能

1. **创建控制器**: `src/main/java/com/kinkle/helloquick/controller/NewController.java`
2. **添加业务逻辑**: `src/main/java/com/kinkle/helloquick/service/NewService.java`
3. **创建测试**: `src/test/java/com/kinkle/helloquick/controller/NewControllerTest.java`
4. **更新配置**: 如需要，修改 `application.properties`

### 数据库集成

1. 添加 JPA 依赖到 `pom.xml`
2. 创建实体类: `src/main/java/com/kinkle/helloquick/entity/`
3. 创建仓库接口: `src/main/java/com/kinkle/helloquick/repository/`
4. 配置数据库连接: `application.properties`

### 安全配置

1. 添加 Spring Security 依赖
2. 创建配置类: `src/main/java/com/kinkle/helloquick/config/SecurityConfig.java`
3. 配置认证和授权规则

## 文件组织

**源代码**: `src/main/java/com/kinkle/helloquick/`

**资源文件**: `src/main/resources/` (配置、静态文件)

**测试代码**: `src/test/java/com/kinkle/helloquick/`

**测试资源**: `src/test/resources/`

## 常见问题

### 热重载
- 使用 `mvn spring-boot:run` 自动重载
- IntelliJ IDEA: 启用 "Make project automatically"
- VS Code: 使用 Spring Boot 扩展

### 调试
- 添加断点到控制器方法
- 使用 IDE 调试模式运行
- 检查 `application.properties` 中的日志级别

### 依赖冲突
- 检查 `pom.xml` 中的版本兼容性
- 使用 `mvn dependency:tree` 查看依赖树
- 阿里云镜像已配置，可加快依赖下载

## 参考信息

**Spring Boot 文档**: https://spring.io/projects/spring-boot

**Lombok 特性**: https://projectlombok.org/features/

**Maven 仓库**: https://mvnrepository.com/
