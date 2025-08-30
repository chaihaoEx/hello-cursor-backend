package com.kinkle.helloquick.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinkle.helloquick.common.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HelloController单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@WebMvcTest(HelloController.class)
@DisplayName("HelloController单元测试")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("健康检查接口测试")
    class HealthTests {

        @Test
        @DisplayName("健康检查应该返回成功状态")
        void shouldReturnSuccessForHealthCheck() throws Exception {
            mockMvc.perform(get("/api/v1/hello/health")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("操作成功"));
        }

        @Test
        @DisplayName("健康检查应该返回正确的响应格式")
        void shouldReturnCorrectResponseFormat() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/v1/hello/health"))
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            Result<?> response = objectMapper.readValue(content, Result.class);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getCode()).isEqualTo(200);
            assertThat(response.getMessage()).isEqualTo("操作成功");
            assertThat(response.getData()).isNull();
        }
    }

    @Nested
    @DisplayName("Hello接口测试")
    class HelloTests {

        @Test
        @DisplayName("无参数Hello请求应该返回默认问候语")
        void shouldReturnDefaultGreetingWithoutName() throws Exception {
            mockMvc.perform(get("/api/v1/hello")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Hello请求处理成功"))
                    .andExpect(jsonPath("$.data.greeting").value("Hello, World!"))
                    .andExpect(jsonPath("$.data.name").isEmpty())
                    .andExpect(jsonPath("$.data.timestamp").exists())
                    .andExpect(jsonPath("$.data.requestId").exists());
        }

        @Test
        @DisplayName("带参数Hello请求应该返回个性化问候语")
        void shouldReturnPersonalizedGreetingWithName() throws Exception {
            String testName = "张三";
            
            mockMvc.perform(get("/api/v1/hello")
                            .param("name", testName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Hello请求处理成功"))
                    .andExpect(jsonPath("$.data.greeting").value("Hello, " + testName + "!"))
                    .andExpect(jsonPath("$.data.name").value(testName))
                    .andExpect(jsonPath("$.data.timestamp").exists())
                    .andExpect(jsonPath("$.data.requestId").exists());
        }

        @Test
        @DisplayName("空字符串参数应该返回个性化问候语")
        void shouldReturnPersonalizedGreetingWithEmptyName() throws Exception {
            mockMvc.perform(get("/api/v1/hello")
                            .param("name", "")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.greeting").value("Hello, !"))
                    .andExpect(jsonPath("$.data.name").value(""));
        }

        @Test
        @DisplayName("特殊字符参数应该正确处理")
        void shouldHandleSpecialCharactersInName() throws Exception {
            String specialName = "李四@#$%";
            
            mockMvc.perform(get("/api/v1/hello")
                            .param("name", specialName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.greeting").value("Hello, " + specialName + "!"))
                    .andExpect(jsonPath("$.data.name").value(specialName));
        }

        @Test
        @DisplayName("长字符串参数应该正确处理")
        void shouldHandleLongNameParameter() throws Exception {
            String longName = "a".repeat(100);
            
            mockMvc.perform(get("/api/v1/hello")
                            .param("name", longName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.greeting").value("Hello, " + longName + "!"))
                    .andExpect(jsonPath("$.data.name").value(longName));
        }
    }

    @Nested
    @DisplayName("响应数据验证测试")
    class ResponseValidationTests {

        @Test
        @DisplayName("响应应该包含有效的时间戳")
        void shouldContainValidTimestamp() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/v1/hello"))
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertThat(content).contains("timestamp");
            assertThat(content).matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");
        }

        @Test
        @DisplayName("响应应该包含有效的请求ID")
        void shouldContainValidRequestId() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/v1/hello"))
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertThat(content).contains("requestId");
            // UUID格式验证
            assertThat(content).matches(".*\"requestId\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\".*");
        }

        @Test
        @DisplayName("每次请求应该生成不同的请求ID")
        void shouldGenerateDifferentRequestIds() throws Exception {
            MvcResult result1 = mockMvc.perform(get("/api/v1/hello"))
                    .andExpect(status().isOk())
                    .andReturn();

            MvcResult result2 = mockMvc.perform(get("/api/v1/hello"))
                    .andExpect(status().isOk())
                    .andReturn();

            String content1 = result1.getResponse().getContentAsString();
            String content2 = result2.getResponse().getContentAsString();

            assertThat(content1).isNotEqualTo(content2);
        }
    }

    @Nested
    @DisplayName("CORS和HTTP头测试")
    class CorsAndHeaderTests {

        @Test
        @DisplayName("应该支持CORS跨域请求")
        void shouldSupportCorsRequests() throws Exception {
            mockMvc.perform(get("/api/v1/hello")
                            .header("Origin", "http://localhost:3000"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "*"));
        }

        @Test
        @DisplayName("应该返回正确的Content-Type")
        void shouldReturnCorrectContentType() throws Exception {
            mockMvc.perform(get("/api/v1/hello"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("Unicode字符参数应该正确处理")
        void shouldHandleUnicodeCharacters() throws Exception {
            String unicodeName = "测试用户🎉";
            
            mockMvc.perform(get("/api/v1/hello")
                            .param("name", unicodeName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.greeting").value("Hello, " + unicodeName + "!"))
                    .andExpect(jsonPath("$.data.name").value(unicodeName));
        }

        @Test
        @DisplayName("数字参数应该正确处理")
        void shouldHandleNumericName() throws Exception {
            String numericName = "12345";
            
            mockMvc.perform(get("/api/v1/hello")
                            .param("name", numericName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.greeting").value("Hello, " + numericName + "!"))
                    .andExpect(jsonPath("$.data.name").value(numericName));
        }
    }
}
