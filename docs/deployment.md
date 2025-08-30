<!-- Generated: 2025-01-08 12:00:00 UTC -->

# 部署文档

Spring Boot 应用的打包和部署指南。

## 打包概述

**打包类型**: 可执行 JAR 文件 (Spring Boot Maven 插件自动配置)

**输出位置**: `target/hello-quick-0.0.1-SNAPSHOT.jar`

**构建配置**: `pom.xml` lines 47-61 (Spring Boot Maven 插件)

## 打包流程

### 标准打包
```bash
mvn clean package                    # 构建生产 JAR
mvn clean package -DskipTests       # 跳过测试打包
```

### 验证打包
```bash
java -jar target/hello-quick-0.0.1-SNAPSHOT.jar
```

## 包类型

**可执行 JAR**: 包含所有依赖的独立运行包

**结构**:
```
hello-quick-0.0.1-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/          # 编译后的类文件
│   └── lib/             # 依赖 JAR 文件
├── META-INF/
│   └── MANIFEST.MF      # Spring Boot 清单
└── org/springframework/boot/loader/  # Spring Boot 加载器
```

## 平台部署

### 本地部署
```bash
# 直接运行
java -jar target/hello-quick-0.0.1-SNAPSHOT.jar

# 后台运行
java -jar target/hello-quick-0.0.1-SNAPSHOT.jar &

# 指定端口
java -jar target/hello-quick-0.0.1-SNAPSHOT.jar --server.port=9090
```

### Docker 部署

**Dockerfile 示例**:
```dockerfile
FROM openjdk:17-jre-slim
COPY target/hello-quick-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

**构建和运行**:
```bash
docker build -t hello-quick .
docker run -p 8080:8080 hello-quick
```

### 云服务器部署

**阿里云/腾讯云**:
1. 上传 JAR 到服务器
2. 配置 JDK 17 环境
3. 使用 systemctl 或 nohup 后台运行
4. 配置反向代理 (Nginx)

**AWS EC2/GCP**:
1. 构建 JAR 包
2. 上传到云存储
3. 使用云实例运行
4. 配置安全组和负载均衡

## 配置管理

### 环境配置

**开发环境**: `application-dev.properties`
**生产环境**: `application-prod.properties`

**运行时指定**:
```bash
java -jar app.jar --spring.profiles.active=prod
```

### 外部配置

**配置文件位置**:
1. `config/application.properties`
2. 当前目录 `application.properties`
3. `classpath:/application.properties`

**命令行参数**:
```bash
java -jar app.jar --server.port=9090 --spring.datasource.url=jdbc:mysql://...
```

## 监控和日志

### 日志配置

**默认配置**: `src/main/resources/application.properties` lines 6-10

**日志级别**:
```properties
logging.level.com.kinkle=DEBUG
logging.level.org.springframework.web=INFO
```

**日志文件**:
```properties
logging.file.name=logs/hello-quick.log
logging.logback.rollingpolicy.max-file-size=10MB
```

### 健康检查

**Spring Boot Actuator**: 添加依赖后访问 `/actuator/health`

**自定义端点**: `/actuator/info`, `/actuator/metrics`

## 故障排除

### 常见问题

**端口占用**:
```bash
netstat -ano | findstr :8080    # Windows
lsof -i :8080                   # Linux/Mac
```

**内存不足**:
```bash
java -Xmx512m -Xms256m -jar app.jar
```

**数据库连接失败**: 检查 `application.properties` 中的数据库配置

**依赖缺失**: 确认 `mvn clean package` 成功完成

## 部署脚本

### Linux 启动脚本
```bash
#!/bin/bash
APP_NAME="hello-quick"
JAR_FILE="target/${APP_NAME}-0.0.1-SNAPSHOT.jar"

# 启动应用
nohup java -jar $JAR_FILE > logs/app.log 2>&1 &

# 获取进程 ID
echo $! > app.pid
```

### Windows 批处理
```batch
@echo off
set APP_NAME=hello-quick
set JAR_FILE=target\%APP_NAME%-0.0.1-SNAPSHOT.jar

start javaw -jar %JAR_FILE%
```

## 参考信息

**Spring Boot 部署**: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html

**Docker 最佳实践**: https://docs.docker.com/develop/dev-best-practices/

**阿里云镜像**: 已配置以提升构建速度
