package com.kinkle.helloquick.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA配置类
 * <p>
 * 配置JPA相关功能，包括审计、事务管理等。
 * 遵循spring-architect.mdc的配置管理原则。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.kinkle.helloquick.**.repository")
@EnableTransactionManagement
public class JpaConfig {
    
    // JPA配置已通过注解完成，无需额外配置
}
