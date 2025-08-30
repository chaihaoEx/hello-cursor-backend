-- ==================================================================
-- Hello Quick 项目默认数据初始化脚本
-- 用于快速初始化开发/测试环境的基础数据
-- ==================================================================

USE `helloquick`;

-- ==================================================================
-- 清理现有数据（谨慎使用）
-- ==================================================================

-- 注意：以下命令会删除所有现有数据，仅在开发环境使用！
-- DELETE FROM user_roles;
-- DELETE FROM users WHERE id > 0;
-- DELETE FROM roles WHERE id > 0;
-- DELETE FROM system_config WHERE id > 0;
-- ALTER TABLE users AUTO_INCREMENT = 1;
-- ALTER TABLE roles AUTO_INCREMENT = 1;

-- ==================================================================
-- 角色数据初始化
-- ==================================================================

INSERT IGNORE INTO `roles` (`id`, `role_name`, `role_code`, `description`, `status`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
(2, '管理员', 'ADMIN', '系统管理员，拥有大部分管理权限', 1),
(3, '普通用户', 'USER', '普通用户，拥有基本操作权限', 1),
(4, '访客', 'GUEST', '访客用户，只读权限', 1);

-- ==================================================================
-- 用户数据初始化
-- ==================================================================

-- 注意：以下密码在实际使用时需要通过BCrypt加密
INSERT IGNORE INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `phone`, `status`) VALUES
(1, 'admin', 'admin@helloquick.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', '13800138000', 1),
(2, 'manager', 'manager@helloquick.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '部门经理', '13800138001', 1),
(3, 'user1', 'user1@helloquick.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '张三', '13800138002', 1),
(4, 'user2', 'user2@helloquick.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '李四', '13800138003', 1),
(5, 'testuser', 'test@helloquick.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '测试用户', '13800138004', 1),
(6, 'guest', 'guest@helloquick.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '访客用户', NULL, 1);

-- ==================================================================
-- 用户角色关联初始化
-- ==================================================================

INSERT IGNORE INTO `user_roles` (`user_id`, `role_id`) VALUES
-- admin用户：超级管理员
(1, 1),
-- manager用户：管理员
(2, 2),
-- user1用户：普通用户
(3, 3),
-- user2用户：普通用户  
(4, 3),
-- testuser用户：普通用户
(5, 3),
-- guest用户：访客
(6, 4);

-- ==================================================================
-- 系统配置初始化
-- ==================================================================

INSERT IGNORE INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `group_name`, `is_encrypted`) VALUES
-- 基础配置
('app.name', 'Hello Quick', 'STRING', '应用名称', 'basic', 0),
('app.version', '0.0.1-SNAPSHOT', 'STRING', '应用版本', 'basic', 0),
('app.description', 'Hello Quick Spring Boot Application', 'STRING', '应用描述', 'basic', 0),
('app.environment', 'development', 'STRING', '运行环境', 'basic', 0),

-- 安全配置
('security.jwt.secret', 'hello-quick-jwt-secret-key-2025', 'STRING', 'JWT密钥', 'security', 1),
('security.jwt.expiration', '86400', 'INTEGER', 'JWT过期时间（秒）', 'security', 0),
('security.password.min-length', '6', 'INTEGER', '密码最小长度', 'security', 0),
('security.session.timeout', '1800', 'INTEGER', '会话超时时间（秒）', 'security', 0),

-- 业务配置
('business.max-users', '1000', 'INTEGER', '最大用户数量', 'business', 0),
('business.default-page-size', '10', 'INTEGER', '默认分页大小', 'business', 0),
('business.max-page-size', '100', 'INTEGER', '最大分页大小', 'business', 0),

-- 上传配置
('upload.max-file-size', '10485760', 'INTEGER', '文件上传最大大小（字节）', 'upload', 0),
('upload.allowed-types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'STRING', '允许上传的文件类型', 'upload', 0),
('upload.storage-path', '/var/uploads', 'STRING', '文件存储路径', 'upload', 0),

-- 邮件配置
('mail.smtp.host', 'smtp.example.com', 'STRING', 'SMTP服务器地址', 'mail', 0),
('mail.smtp.port', '587', 'INTEGER', 'SMTP服务器端口', 'mail', 0),
('mail.smtp.username', 'noreply@helloquick.com', 'STRING', 'SMTP用户名', 'mail', 0),
('mail.smtp.password', 'your-email-password', 'STRING', 'SMTP密码', 'mail', 1),

-- 缓存配置
('cache.default-ttl', '3600', 'INTEGER', '默认缓存TTL（秒）', 'cache', 0),
('cache.max-size', '1000', 'INTEGER', '最大缓存条目数', 'cache', 0);

-- ==================================================================
-- 数据验证和统计
-- ==================================================================

-- 显示初始化结果
SELECT '=== 数据库初始化完成 ===' AS message;

-- 统计信息
SELECT 
    '用户总数' AS metric,
    COUNT(*) AS value
FROM users
WHERE status = 1
UNION ALL
SELECT 
    '角色总数' AS metric,
    COUNT(*) AS value
FROM roles
WHERE status = 1
UNION ALL
SELECT 
    '配置项总数' AS metric,
    COUNT(*) AS value
FROM system_config
UNION ALL
SELECT 
    '用户角色关联数' AS metric,
    COUNT(*) AS value
FROM user_roles;

-- 用户角色分布
SELECT 
    r.role_name AS 角色名称,
    COUNT(ur.user_id) AS 用户数量
FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
WHERE r.status = 1
GROUP BY r.id, r.role_name
ORDER BY r.id;

-- ==================================================================
-- 使用说明
-- ==================================================================

/*
默认账户信息：

1. 超级管理员
   用户名: admin
   邮箱: admin@helloquick.com
   密码: secret (需要BCrypt加密)
   
2. 部门经理  
   用户名: manager
   邮箱: manager@helloquick.com
   密码: secret (需要BCrypt加密)
   
3. 普通用户
   用户名: user1, user2, testuser
   密码: secret (需要BCrypt加密)
   
4. 访客用户
   用户名: guest
   密码: secret (需要BCrypt加密)

注意事项：
1. 所有密码都需要在Spring Boot应用中使用BCrypt重新加密
2. 生产环境请修改默认密码和JWT密钥
3. 系统配置中的敏感信息已标记为加密存储
4. 建议定期备份数据库和日志清理

执行方式：
mysql -u root -p < init-data.sql
或
kubectl exec mysql-0 -n mysql -- mysql -u root -pmysql123 < init-data.sql
*/
