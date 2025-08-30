package com.kinkle.helloquick.controller;

import com.kinkle.helloquick.common.result.Result;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Hello控制器
 * <p>
 * 提供基础的Hello API接口。
 * 遵循spring-architect.mdc的单一职责原则。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/hello")
@CrossOrigin(origins = "*")
public class HelloController {

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public Result<Void> health() {
        log.info("REST健康检查请求");
        return Result.success();
    }

    /**
     * Hello接口
     *
     * @param name 用户名（可选）
     * @return Hello响应
     */
    @GetMapping
    public Result<HelloResponse> hello(@RequestParam(required = false) String name) {
        log.info("REST Hello请求，参数: {}", name);
        
        String greeting = name != null ? "Hello, " + name + "!" : "Hello, World!";
        
        HelloResponse response = HelloResponse.builder()
                .greeting(greeting)
                .name(name)
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID().toString())
                .build();

        return Result.success(response, "Hello请求处理成功");
    }

    /**
     * Hello响应类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelloResponse {
        private String greeting;
        private String name;
        private LocalDateTime timestamp;
        private String requestId;
    }
}