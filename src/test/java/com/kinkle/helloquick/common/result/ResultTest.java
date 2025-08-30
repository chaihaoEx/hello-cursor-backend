package com.kinkle.helloquick.common.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result类单元测试
 * <p>
 * 测试覆盖率目标：95%+
 * 遵循pr-review.mdc的测试质量标准。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("Result 统一响应结果类测试")
class ResultTest {

    @Nested
    @DisplayName("成功响应测试")
    class SuccessTests {

        @Test
        @DisplayName("无参成功响应")
        void testSuccessWithoutData() {
            // When
            Result<Void> result = Result.success();

            // Then
            assertAll(
                () -> assertEquals(ResultCode.SUCCESS.getCode(), result.getCode()),
                () -> assertEquals(ResultCode.SUCCESS.getMessage(), result.getMessage()),
                () -> assertNull(result.getData()),
                () -> assertNotNull(result.getTimestamp()),
                () -> assertNull(result.getRequestId()),
                () -> assertTrue(result.isSuccess())
            );
        }

        @Test
        @DisplayName("带数据的成功响应")
        void testSuccessWithData() {
            // Given
            String testData = "test data";

            // When
            Result<String> result = Result.success(testData);

            // Then
            assertAll(
                () -> assertEquals(ResultCode.SUCCESS.getCode(), result.getCode()),
                () -> assertEquals(ResultCode.SUCCESS.getMessage(), result.getMessage()),
                () -> assertEquals(testData, result.getData()),
                () -> assertNotNull(result.getTimestamp()),
                () -> assertTrue(result.isSuccess())
            );
        }

        @Test
        @DisplayName("带数据和自定义消息的成功响应")
        void testSuccessWithDataAndMessage() {
            // Given
            String testData = "test data";
            String customMessage = "操作成功完成";

            // When
            Result<String> result = Result.success(testData, customMessage);

            // Then
            assertAll(
                () -> assertEquals(ResultCode.SUCCESS.getCode(), result.getCode()),
                () -> assertEquals(customMessage, result.getMessage()),
                () -> assertEquals(testData, result.getData()),
                () -> assertNotNull(result.getTimestamp()),
                () -> assertTrue(result.isSuccess())
            );
        }
    }

    @Nested
    @DisplayName("失败响应测试")
    class FailureTests {

        @Test
        @DisplayName("使用ResultCode的失败响应")
        void testFailureWithResultCode() {
            // When
            Result<Void> result = Result.failure(ResultCode.PARAM_ERROR);

            // Then
            assertAll(
                () -> assertEquals(ResultCode.PARAM_ERROR.getCode(), result.getCode()),
                () -> assertEquals(ResultCode.PARAM_ERROR.getMessage(), result.getMessage()),
                () -> assertNull(result.getData()),
                () -> assertNotNull(result.getTimestamp()),
                () -> assertFalse(result.isSuccess())
            );
        }

        @Test
        @DisplayName("使用ResultCode和自定义消息的失败响应")
        void testFailureWithResultCodeAndMessage() {
            // Given
            String customMessage = "用户名已存在";

            // When
            Result<Void> result = Result.failure(ResultCode.DATA_EXISTS, customMessage);

            // Then
            assertAll(
                () -> assertEquals(ResultCode.DATA_EXISTS.getCode(), result.getCode()),
                () -> assertEquals(customMessage, result.getMessage()),
                () -> assertNull(result.getData()),
                () -> assertNotNull(result.getTimestamp()),
                () -> assertFalse(result.isSuccess())
            );
        }

        @Test
        @DisplayName("使用状态码和消息的失败响应")
        void testFailureWithCodeAndMessage() {
            // Given
            int errorCode = 500;
            String errorMessage = "服务器内部错误";

            // When
            Result<Void> result = Result.failure(errorCode, errorMessage);

            // Then
            assertAll(
                () -> assertEquals(errorCode, result.getCode()),
                () -> assertEquals(errorMessage, result.getMessage()),
                () -> assertNull(result.getData()),
                () -> assertNotNull(result.getTimestamp()),
                () -> assertFalse(result.isSuccess())
            );
        }
    }

    @Nested
    @DisplayName("工具方法测试")
    class UtilityTests {

        @Test
        @DisplayName("设置请求ID")
        void testWithRequestId() {
            // Given
            String requestId = "test-request-id";
            Result<String> result = Result.success("test");

            // When
            Result<String> resultWithId = result.withRequestId(requestId);

            // Then
            assertAll(
                () -> assertSame(result, resultWithId), // 返回同一个对象
                () -> assertEquals(requestId, result.getRequestId())
            );
        }

        @Test
        @DisplayName("isSuccess方法测试")
        void testIsSuccess() {
            // Given & When & Then
            assertTrue(Result.success().isSuccess());
            assertFalse(Result.failure(ResultCode.PARAM_ERROR).isSuccess());
            assertFalse(Result.failure(500, "error").isSuccess());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("null数据测试")
        void testWithNullData() {
            // When
            Result<Object> result = Result.success(null);

            // Then
            assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertNull(result.getData())
            );
        }

        @Test
        @DisplayName("空字符串消息测试")
        void testWithEmptyMessage() {
            // When
            Result<String> result = Result.success("data", "");

            // Then
            assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals("", result.getMessage())
            );
        }

        @Test
        @DisplayName("null消息测试")
        void testWithNullMessage() {
            // When
            Result<String> result = Result.success("data", null);

            // Then
            assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertNull(result.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Builder模式测试")
    class BuilderTests {

        @Test
        @DisplayName("Builder构建测试")
        void testBuilderPattern() {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();
            String requestId = "builder-test-id";

            // When
            Result<String> result = Result.<String>builder()
                    .code(200)
                    .message("success")
                    .data("test data")
                    .timestamp(timestamp)
                    .requestId(requestId)
                    .build();

            // Then
            assertAll(
                () -> assertEquals(200, result.getCode()),
                () -> assertEquals("success", result.getMessage()),
                () -> assertEquals("test data", result.getData()),
                () -> assertEquals(timestamp, result.getTimestamp()),
                () -> assertEquals(requestId, result.getRequestId()),
                () -> assertTrue(result.isSuccess())
            );
        }

        @Test
        @DisplayName("Builder默认值测试")
        void testBuilderDefaults() {
            // When
            Result<String> result = Result.<String>builder()
                    .code(200)
                    .message("test")
                    .build();

            // Then
            assertAll(
                () -> assertNotNull(result.getTimestamp()),
                () -> assertNull(result.getRequestId()),
                () -> assertNull(result.getData())
            );
        }
    }
}
