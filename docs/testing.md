<!-- Generated: 2025-01-08 12:00:00 UTC -->

# 测试文档

使用 Spring Boot Test 和 JUnit 5 进行单元测试和集成测试。

## 测试概览

**测试框架**: JUnit 5 + Spring Boot Test (通过 `pom.xml` lines 34-38)

**测试位置**: `src/test/java/com/kinkle/helloquick/`

**主要测试**: `HelloQuickApplicationTests.java` (应用上下文测试)

## 测试类型

**单元测试**: 独立的类和方法测试

**集成测试**: Spring 应用上下文测试

**Web 测试**: REST API 端点测试 (待扩展)

## 运行测试

### 全部测试
```bash
mvn test                    # 运行所有测试
mvn clean test              # 清理后运行测试
```

### 特定测试类
```bash
mvn test -Dtest=HelloQuickApplicationTests
```

### 跳过测试
```bash
mvn clean compile -DskipTests
mvn clean package -DskipTests
```

## 测试文件组织

**测试类**: `src/test/java/com/kinkle/helloquick/HelloQuickApplicationTests.java`

**测试方法**: `contextLoads()` (验证 Spring 应用上下文正常启动)

**命名约定**: `*Tests.java` 后缀，与主类包结构一致

## 扩展测试

### Controller 测试示例
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testHelloEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/hello", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### Mock 测试示例
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    // 使用 Mockito 进行单元测试
}
```

## 测试配置

**Spring Profile**: 默认使用 `application.properties`

**测试专用配置**: 可在 `src/test/resources/` 添加 `application-test.properties`

**随机端口**: 避免端口冲突 (Web 测试时使用 `RANDOM_PORT`)

## 构建系统集成

**Maven Surefire**: 自动运行测试 (`pom.xml` 继承自 Spring Boot)

**测试报告**: `target/surefire-reports/` (XML 和 HTML 格式)

**覆盖率**: 可添加 JaCoCo 插件进行代码覆盖率分析
