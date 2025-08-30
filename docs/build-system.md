<!-- Generated: 2025-01-08 12:00:00 UTC -->

# 构建系统

基于 Maven 的构建系统，已配置阿里云镜像以提升构建速度。

## 构建配置

**主配置**: `pom.xml` (完整的 Maven 项目配置)

**Spring Boot 版本**: 3.5.5 (`pom.xml` lines 9-13)

**Java 版本**: 17 (`pom.xml` lines 23-25)

## 构建流程

### 标准构建
```bash
mvn clean compile  # 清理并编译
mvn clean package  # 打包为 JAR
mvn clean install  # 安装到本地仓库
```

### 快速开发
```bash
mvn spring-boot:run  # 直接运行应用
mvn compile          # 仅编译
```

### 测试相关
```bash
mvn test            # 运行所有测试
mvn clean test       # 清理后运行测试
```

## 平台设置

**Windows**: 使用 PowerShell 或 cmd (项目根目录执行 Maven 命令)

**Linux/macOS**: 使用终端 (项目根目录执行 `./mvnw` 或 `mvn`)

**JDK 要求**: Java 17+ (Maven 自动检测 `pom.xml` 配置)

## 依赖管理

**阿里云镜像**: 已配置中央仓库和公共仓库 (`pom.xml` lines 28-80)

**核心依赖**:
- Spring Boot Starter Web (`pom.xml` lines 29-32)
- Spring Boot Starter Test (`pom.xml` lines 34-38)
- Lombok (`pom.xml` lines 40-44)

## 构建输出

**JAR 文件**: `target/hello-quick-0.0.1-SNAPSHOT.jar`

**类文件**: `target/classes/` (编译后的字节码)

**测试报告**: `target/surefire-reports/` (测试结果)

## 故障排除

**依赖下载失败**: 检查网络连接和阿里云镜像配置

**编译错误**: 确认 JDK 17 已安装，`java -version` 检查版本

**端口占用**: 修改 `src/main/resources/application.properties` 中 `server.port`

**构建速度慢**: 阿里云镜像已配置，可考虑本地 Maven 仓库缓存
