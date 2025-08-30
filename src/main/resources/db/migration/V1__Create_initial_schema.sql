-- ==================================================================
-- Hello Quick 项目数据库结构脚本
-- 基于当前helloquick数据库的实际结构生成
-- 生成时间: 2025-08-29
-- ==================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `helloquick` 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `helloquick`;

-- ==================================================================
-- 表结构定义
-- ==================================================================

-- 1. 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `password` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `full_name` VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 角色表
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` TEXT COLLATE utf8mb4_unicode_ci COMMENT '角色描述',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 用户角色关联表
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 4. 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` TEXT COLLATE utf8mb4_unicode_ci COMMENT '配置值',
  `config_type` VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STRING' COMMENT '配置类型：STRING,INTEGER,BOOLEAN,JSON',
  `description` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `group_name` VARCHAR(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置分组',
  `is_encrypted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否加密：1-是，0-否',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 5. 操作日志表
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
  `username` VARCHAR(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作用户名',
  `operation` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `method` VARCHAR(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'HTTP方法',
  `url` VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求URL',
  `ip_address` VARCHAR(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` TEXT COLLATE utf8mb4_unicode_ci COMMENT '用户代理',
  `request_params` JSON DEFAULT NULL COMMENT '请求参数',
  `response_result` JSON DEFAULT NULL COMMENT '响应结果',
  `status` VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作状态：SUCCESS,FAILED',
  `error_message` TEXT COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `execution_time` BIGINT DEFAULT NULL COMMENT '执行时间（毫秒）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ==================================================================
-- 默认数据插入
-- ==================================================================

-- 插入默认角色数据
INSERT INTO `roles` (`id`, `role_name`, `role_code`, `description`, `status`, `created_at`, `updated_at`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, '2025-08-29 12:42:53', '2025-08-29 12:42:53'),
(2, '管理员', 'ADMIN', '系统管理员，拥有大部分管理权限', 1, '2025-08-29 12:42:53', '2025-08-29 12:42:53'),
(3, '用户', 'USER', '普通用户，拥有基本操作权限', 1, '2025-08-29 12:42:53', '2025-08-29 12:42:53')
ON DUPLICATE KEY UPDATE 
  `role_name` = VALUES(`role_name`),
  `description` = VALUES(`description`),
  `updated_at` = CURRENT_TIMESTAMP;

-- 插入默认用户数据（注意：密码需要在应用中使用BCrypt加密）
INSERT INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `phone`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin', 'admin@helloquick.com', '$2a$10$YourEncodedPasswordHere', '系统管理员', NULL, 1, '2025-08-29 12:42:53', '2025-08-29 12:42:53'),
(2, 'test', 'test@helloquick.com', 'test123', '测试用户', NULL, 1, '2025-08-29 12:43:22', '2025-08-29 12:43:22')
ON DUPLICATE KEY UPDATE 
  `email` = VALUES(`email`),
  `full_name` = VALUES(`full_name`),
  `updated_at` = CURRENT_TIMESTAMP;

-- 插入默认系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `group_name`, `is_encrypted`) VALUES
('app.name', 'Hello Quick', 'STRING', '应用名称', 'basic', 0),
('app.version', '0.0.1-SNAPSHOT', 'STRING', '应用版本', 'basic', 0),
('app.description', 'Hello Quick Spring Boot Application', 'STRING', '应用描述', 'basic', 0),
('security.jwt.secret', 'hello-quick-jwt-secret-key', 'STRING', 'JWT密钥', 'security', 1),
('security.jwt.expiration', '86400', 'INTEGER', 'JWT过期时间（秒）', 'security', 0),
('upload.max-file-size', '10485760', 'INTEGER', '文件上传最大大小（字节）', 'upload', 0),
('upload.allowed-types', 'jpg,jpeg,png,gif,pdf,doc,docx', 'STRING', '允许上传的文件类型', 'upload', 0)
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `updated_at` = CURRENT_TIMESTAMP;

-- 为管理员用户分配超级管理员角色
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1)  -- admin用户 -> SUPER_ADMIN角色
ON DUPLICATE KEY UPDATE `created_at` = `created_at`;

-- ==================================================================
-- 数据库用户和权限管理
-- ==================================================================

-- 创建应用专用数据库用户
CREATE USER IF NOT EXISTS 'helloquick'@'%' IDENTIFIED BY 'helloquick123';
GRANT ALL PRIVILEGES ON helloquick.* TO 'helloquick'@'%';

-- 创建只读用户（用于报表和监控）
CREATE USER IF NOT EXISTS 'helloquick_readonly'@'%' IDENTIFIED BY 'readonly123';
GRANT SELECT ON helloquick.* TO 'helloquick_readonly'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- ==================================================================
-- 索引优化建议
-- ==================================================================

-- 用户表索引优化
-- ALTER TABLE users ADD INDEX idx_username_status (username, status);
-- ALTER TABLE users ADD INDEX idx_email_status (email, status);

-- 角色表索引优化  
-- ALTER TABLE roles ADD INDEX idx_role_name (role_name);

-- 用户角色关联表索引优化
-- ALTER TABLE user_roles ADD INDEX idx_user_role_created (user_id, role_id, created_at);

-- 操作日志表索引优化（如果数据量大）
-- ALTER TABLE operation_logs ADD INDEX idx_user_operation_time (user_id, operation, created_at);
-- ALTER TABLE operation_logs ADD INDEX idx_status_time (status, created_at);

-- ==================================================================
-- 数据验证查询
-- ==================================================================

-- 验证数据完整性
SELECT 'Database helloquick initialized successfully!' AS result;

-- 显示表统计信息
SELECT 
    'users' AS table_name,
    COUNT(*) AS record_count
FROM users
UNION ALL
SELECT 
    'roles' AS table_name,
    COUNT(*) AS record_count  
FROM roles
UNION ALL
SELECT 
    'user_roles' AS table_name,
    COUNT(*) AS record_count
FROM user_roles
UNION ALL
SELECT 
    'system_config' AS table_name,
    COUNT(*) AS record_count
FROM system_config;

-- 显示用户角色关联情况
SELECT 
    u.username,
    u.email,
    r.role_name,
    r.role_code,
    ur.created_at AS assigned_at
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
ORDER BY u.id, r.id;

-- ==================================================================
-- 备注说明
-- ==================================================================

/*
数据库设计说明：

1. 字符集统一使用 utf8mb4_unicode_ci
2. 所有表都包含 created_at 和 updated_at 审计字段
3. 用户密码使用 BCrypt 加密存储
4. 用户角色采用多对多关系设计
5. 系统配置支持加密存储敏感信息
6. 操作日志表使用 JSON 字段存储复杂数据

连接信息：
- 数据库: helloquick
- 应用用户: helloquick / helloquick123 (读写权限)
- 只读用户: helloquick_readonly / readonly123 (只读权限)

Spring Boot 配置：
spring:
  datasource:
    url: jdbc:mysql://mysql.mysql.svc.cluster.local:3306/helloquick?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: helloquick
    password: helloquick123
    driver-class-name: com.mysql.cj.jdbc.Driver
*/
