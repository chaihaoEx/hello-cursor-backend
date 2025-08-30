package com.kinkle.helloquick;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Hello Quick 应用程序测试类
 * <p>
 * 验证Spring Boot应用上下文是否正确加载。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@SpringBootTest
@ActiveProfiles("test")
class HelloQuickApplicationTests {

    /**
     * 应用上下文加载测试
     */
    @Test
    void contextLoads() {
        // Spring Boot应用上下文加载测试
        // 如果上下文无法加载，此测试将失败
    }

}
