package com.kinkle.helloquick.common.util;

/**
 * 缓存Key工具类
 * 统一管理Redis缓存的Key格式，所有key都在内部实现，对外提供方法获取完整的key
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
public class CacheKeyUtil {

    /**
     * 应用前缀
     */
    private static final String APP_PREFIX = "hello-quick";

    /**
     * 分隔符
     */
    private static final String SEPARATOR = ":";

    /**
     * 用户缓存前缀
     */
    private static final String USER_PREFIX = APP_PREFIX + SEPARATOR + "user";

    /**
     * 用户统计缓存前缀
     */
    private static final String USER_STATS_PREFIX = APP_PREFIX + SEPARATOR + "user-stats";

    /**
     * 会话缓存前缀
     */
    private static final String SESSION_PREFIX = APP_PREFIX + SEPARATOR + "session";

    /**
     * 限流缓存前缀
     */
    private static final String RATE_LIMIT_PREFIX = APP_PREFIX + SEPARATOR + "rate-limit";

    /**
     * 验证码缓存前缀
     */
    private static final String VERIFICATION_PREFIX = APP_PREFIX + SEPARATOR + "verification";

    /**
     * 测试缓存前缀
     */
    private static final String TEST_PREFIX = APP_PREFIX + SEPARATOR + "test";

    /**
     * 私有构造函数，防止实例化
     */
    private CacheKeyUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========================= 用户相关缓存Key =========================

    /**
     * 生成用户缓存Key
     * 
     * @param userId 用户ID
     * @return 缓存Key: hello-quick:user:{userId}
     */
    public static String getUserKey(Long userId) {
        return USER_PREFIX + SEPARATOR + userId;
    }

    /**
     * 生成用户名缓存Key
     * 
     * @param username 用户名
     * @return 缓存Key: hello-quick:user:username:{username}
     */
    public static String getUserByUsernameKey(String username) {
        return USER_PREFIX + SEPARATOR + "username" + SEPARATOR + username;
    }

    /**
     * 生成用户统计缓存Key
     * 
     * @param statType 统计类型
     * @return 缓存Key: hello-quick:user-stats:{statType}
     */
    public static String getUserStatsKey(String statType) {
        return USER_STATS_PREFIX + SEPARATOR + statType;
    }

    // ========================= 会话相关缓存Key =========================

    /**
     * 生成会话缓存Key
     * 
     * @param sessionId 会话ID
     * @return 缓存Key: hello-quick:session:{sessionId}
     */
    public static String getSessionKey(String sessionId) {
        return SESSION_PREFIX + SEPARATOR + sessionId;
    }

    /**
     * 生成用户会话列表Key
     * 
     * @param userId 用户ID
     * @return 缓存Key: hello-quick:session:user:{userId}
     */
    public static String getUserSessionsKey(Long userId) {
        return SESSION_PREFIX + SEPARATOR + "user" + SEPARATOR + userId;
    }

    // ========================= 限流相关缓存Key =========================

    /**
     * 生成API限流缓存Key
     * 
     * @param identifier 标识符（如IP地址、用户ID等）
     * @param resource   资源标识
     * @return 缓存Key: hello-quick:rate-limit:{resource}:{identifier}
     */
    public static String getRateLimitKey(String identifier, String resource) {
        return RATE_LIMIT_PREFIX + SEPARATOR + resource + SEPARATOR + identifier;
    }

    /**
     * 生成IP限流缓存Key
     * 
     * @param ipAddress IP地址
     * @param endpoint  接口端点
     * @return 缓存Key: hello-quick:rate-limit:ip:{endpoint}:{ipAddress}
     */
    public static String getIpRateLimitKey(String ipAddress, String endpoint) {
        return RATE_LIMIT_PREFIX + SEPARATOR + "ip" + SEPARATOR + endpoint + SEPARATOR + ipAddress;
    }

    /**
     * 生成用户限流缓存Key
     * 
     * @param userId   用户ID
     * @param endpoint 接口端点
     * @return 缓存Key: hello-quick:rate-limit:user:{endpoint}:{userId}
     */
    public static String getUserRateLimitKey(Long userId, String endpoint) {
        return RATE_LIMIT_PREFIX + SEPARATOR + "user" + SEPARATOR + endpoint + SEPARATOR + userId;
    }

    // ========================= 验证码相关缓存Key =========================

    /**
     * 生成验证码缓存Key
     * 
     * @param type       验证码类型（如：login, register, reset-password）
     * @param identifier 标识符（如：手机号、邮箱）
     * @return 缓存Key: hello-quick:verification:{type}:{identifier}
     */
    public static String getVerificationCodeKey(String type, String identifier) {
        return VERIFICATION_PREFIX + SEPARATOR + type + SEPARATOR + identifier;
    }

    /**
     * 生成短信验证码Key
     * 
     * @param phone 手机号
     * @param type  验证码类型
     * @return 缓存Key: hello-quick:verification:sms:{type}:{phone}
     */
    public static String getSmsCodeKey(String phone, String type) {
        return VERIFICATION_PREFIX + SEPARATOR + "sms" + SEPARATOR + type + SEPARATOR + phone;
    }

    /**
     * 生成邮箱验证码Key
     * 
     * @param email 邮箱
     * @param type  验证码类型
     * @return 缓存Key: hello-quick:verification:email:{type}:{email}
     */
    public static String getEmailCodeKey(String email, String type) {
        return VERIFICATION_PREFIX + SEPARATOR + "email" + SEPARATOR + type + SEPARATOR + email;
    }

    // ========================= 测试相关缓存Key =========================

    /**
     * 生成测试缓存Key
     * 
     * @param key 测试key
     * @return 缓存Key: hello-quick:test:{key}
     */
    public static String getTestKey(String key) {
        return TEST_PREFIX + SEPARATOR + key;
    }

    /**
     * 生成Ping测试Key
     * 
     * @return 缓存Key: hello-quick:test:ping
     */
    public static String getPingTestKey() {
        return TEST_PREFIX + SEPARATOR + "ping";
    }

    /**
     * 生成统计测试Key
     * 
     * @param statType 统计类型
     * @return 缓存Key: hello-quick:test:stats:{statType}
     */
    public static String getStatsTestKey(String statType) {
        return TEST_PREFIX + SEPARATOR + "stats" + SEPARATOR + statType;
    }

    // ========================= 业务相关缓存Key =========================

    /**
     * 生成角色缓存Key
     * 
     * @param roleId 角色ID
     * @return 缓存Key: hello-quick:role:{roleId}
     */
    public static String getRoleKey(Long roleId) {
        return APP_PREFIX + SEPARATOR + "role" + SEPARATOR + roleId;
    }

    /**
     * 生成用户角色缓存Key
     * 
     * @param userId 用户ID
     * @return 缓存Key: hello-quick:user-role:{userId}
     */
    public static String getUserRoleKey(Long userId) {
        return APP_PREFIX + SEPARATOR + "user-role" + SEPARATOR + userId;
    }

    /**
     * 生成权限缓存Key
     * 
     * @param userId 用户ID
     * @return 缓存Key: hello-quick:permission:{userId}
     */
    public static String getPermissionKey(Long userId) {
        return APP_PREFIX + SEPARATOR + "permission" + SEPARATOR + userId;
    }

    /**
     * 生成配置缓存Key
     * 
     * @param configKey 配置键
     * @return 缓存Key: hello-quick:config:{configKey}
     */
    public static String getConfigKey(String configKey) {
        return APP_PREFIX + SEPARATOR + "config" + SEPARATOR + configKey;
    }

    // ========================= Spring Cache注解支持 =========================

    /**
     * 生成Spring Cache注解使用的Key表达式
     * 用于@Cacheable、@CacheEvict等注解的key属性
     * 
     * @param keyPattern 键模式，支持SpEL表达式
     * @return 完整的Key表达式
     */
    public static String getCacheKeyExpression(String keyPattern) {
        return APP_PREFIX + SEPARATOR + "cache" + SEPARATOR + keyPattern;
    }

    /**
     * 生成用户缓存Key（用于Spring Cache注解）
     * 
     * @param userId 用户ID
     * @return 缓存Key: hello-quick:cache:user:#{userId}
     */
    public static String getUserCacheKey(Long userId) {
        return APP_PREFIX + SEPARATOR + "cache" + SEPARATOR + "user" + SEPARATOR + userId;
    }

    /**
     * 生成用户统计缓存Key（用于Spring Cache注解）
     * 
     * @param statType 统计类型
     * @return 缓存Key: hello-quick:cache:user-stats:#{statType}
     */
    public static String getUserStatsCacheKey(String statType) {
        return APP_PREFIX + SEPARATOR + "cache" + SEPARATOR + "user-stats" + SEPARATOR + statType;
    }

    /**
     * 生成会话缓存Key（用于Spring Cache注解）
     * 
     * @param sessionId 会话ID
     * @return 缓存Key: hello-quick:cache:session:#{sessionId}
     */
    public static String getSessionCacheKey(String sessionId) {
        return APP_PREFIX + SEPARATOR + "cache" + SEPARATOR + "session" + SEPARATOR + sessionId;
    }

    /**
     * 生成限流缓存Key（用于Spring Cache注解）
     * 
     * @param identifier 标识符
     * @param resource   资源
     * @return 缓存Key: hello-quick:cache:rate-limit:#{resource}:#{identifier}
     */
    public static String getRateLimitCacheKey(String identifier, String resource) {
        return APP_PREFIX + SEPARATOR + "cache" + SEPARATOR + "rate-limit" + SEPARATOR + resource + SEPARATOR + identifier;
    }

    // ========================= 缓存键模式常量 =========================

    /**
     * 用户缓存键模式
     */
    public static final String USER_CACHE_KEY_PATTERN = "user::#{#id}";

    /**
     * 用户名缓存键模式
     */
    public static final String USERNAME_CACHE_KEY_PATTERN = "user::username::#{#username}";

    /**
     * 用户统计缓存键模式
     */
    public static final String USER_STATS_CACHE_KEY_PATTERN = "user-stats::#{#statType}";

    /**
     * 会话缓存键模式
     */
    public static final String SESSION_CACHE_KEY_PATTERN = "session::#{#sessionId}";

    /**
     * 限流缓存键模式
     */
    public static final String RATE_LIMIT_CACHE_KEY_PATTERN = "rate-limit::#{#resource}::#{#identifier}";

    // ========================= 工具方法 =========================

    /**
     * 构建缓存键
     * 
     * @param parts 键的各个部分
     * @return 完整的缓存键
     */
    public static String buildKey(String... parts) {
        if (parts == null || parts.length == 0) {
            return APP_PREFIX;
        }
        StringBuilder keyBuilder = new StringBuilder(APP_PREFIX);
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                keyBuilder.append(SEPARATOR).append(part);
            }
        }
        return keyBuilder.toString();
    }

    /**
     * 验证缓存键格式
     * 
     * @param key 缓存键
     * @return true如果格式正确，false否则
     */
    public static boolean isValidKey(String key) {
        return key != null && key.startsWith(APP_PREFIX + SEPARATOR) && key.length() > APP_PREFIX.length() + 1;
    }

    /**
     * 获取缓存键的命名空间
     * 
     * @param key 缓存键
     * @return 命名空间，如果格式不正确返回null
     */
    public static String getNamespace(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        String withoutPrefix = key.substring(APP_PREFIX.length() + 1);
        int firstSeparatorIndex = withoutPrefix.indexOf(SEPARATOR);
        if (firstSeparatorIndex > 0) {
            return withoutPrefix.substring(0, firstSeparatorIndex);
        }
        return withoutPrefix;
    }
}
