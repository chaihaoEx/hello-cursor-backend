package com.kinkle.helloquick.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用配置属性类
 * <p>
 * 绑定自定义的应用配置属性，避免YAML未知属性警告。
 * 遵循spring-architect.mdc的配置管理原则。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 安全配置
     */
    private Security security = new Security();

    @Data
    public static class Security {
        
        /**
         * JWT配置
         */
        private Jwt jwt = new Jwt();
        
        /**
         * 允许的IP地址列表
         */
        private List<String> allowedIps = List.of("127.0.0.1", "::1");

        @Data
        public static class Jwt {
            
            /**
             * JWT密钥
             */
            private String secret = "hello-quick-jwt-secret-key";
            
            /**
             * JWT过期时间（秒）
             */
            private long expiration = 86400;
        }
    }
}
