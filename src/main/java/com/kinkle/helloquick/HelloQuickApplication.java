package com.kinkle.helloquick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello Quick 应用程序主类
 * <p>
 * 基于Spring Boot 3.5.5的快速启动项目，提供REST API服务。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@SpringBootApplication
public class HelloQuickApplication {

    /**
     * 应用程序入口点
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(HelloQuickApplication.class, args);
    }

}
