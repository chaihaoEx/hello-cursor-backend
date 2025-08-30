package com.kinkle.helloquick.common.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultCode枚举类单元测试
 * <p>
 * 测试覆盖率目标：95%+
 * 遵循pr-review.mdc的测试质量标准。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("ResultCode 响应状态码枚举测试")
class ResultCodeTest {

    @Nested
    @DisplayName("基本功能测试")
    class BasicTests {

        @Test
        @DisplayName("SUCCESS状态码测试")
        void testSuccessCode() {
            // When & Then
            assertAll(
                () -> assertEquals(200, ResultCode.SUCCESS.getCode()),
                () -> assertEquals("操作成功", ResultCode.SUCCESS.getMessage()),
                () -> assertNotNull(ResultCode.SUCCESS.name())
            );
        }

        @Test
        @DisplayName("FAILURE状态码测试")
        void testFailureCode() {
            // When & Then
            assertAll(
                () -> assertEquals(400, ResultCode.FAILURE.getCode()),
                () -> assertEquals("操作失败", ResultCode.FAILURE.getMessage())
            );
        }

        @Test
        @DisplayName("PARAM_ERROR状态码测试")
        void testParamErrorCode() {
            // When & Then
            assertAll(
                () -> assertEquals(422, ResultCode.PARAM_ERROR.getCode()),
                () -> assertEquals("参数错误", ResultCode.PARAM_ERROR.getMessage())
            );
        }

        @Test
        @DisplayName("DATA_NOT_FOUND状态码测试")
        void testDataNotFoundCode() {
            // When & Then
            assertAll(
                () -> assertEquals(404, ResultCode.DATA_NOT_FOUND.getCode()),
                () -> assertEquals("数据不存在", ResultCode.DATA_NOT_FOUND.getMessage())
            );
        }

        @Test
        @DisplayName("DATA_EXISTS状态码测试")
        void testDataExistsCode() {
            // When & Then
            assertAll(
                () -> assertEquals(409, ResultCode.DATA_EXISTS.getCode()),
                () -> assertEquals("数据已存在", ResultCode.DATA_EXISTS.getMessage())
            );
        }

        @Test
        @DisplayName("UNAUTHORIZED状态码测试")
        void testUnauthorizedCode() {
            // When & Then
            assertAll(
                () -> assertEquals(401, ResultCode.UNAUTHORIZED.getCode()),
                () -> assertEquals("未授权", ResultCode.UNAUTHORIZED.getMessage())
            );
        }

        @Test
        @DisplayName("FORBIDDEN状态码测试")
        void testForbiddenCode() {
            // When & Then
            assertAll(
                () -> assertEquals(403, ResultCode.FORBIDDEN.getCode()),
                () -> assertEquals("禁止访问", ResultCode.FORBIDDEN.getMessage())
            );
        }

        @Test
        @DisplayName("INTERNAL_SERVER_ERROR状态码测试")
        void testInternalServerErrorCode() {
            // When & Then
            assertAll(
                () -> assertEquals(500, ResultCode.INTERNAL_SERVER_ERROR.getCode()),
                () -> assertEquals("服务器内部错误", ResultCode.INTERNAL_SERVER_ERROR.getMessage())
            );
        }

        @Test
        @DisplayName("SERVICE_UNAVAILABLE状态码测试")
        void testServiceUnavailableCode() {
            // When & Then
            assertAll(
                () -> assertEquals(503, ResultCode.SERVICE_UNAVAILABLE.getCode()),
                () -> assertEquals("服务不可用", ResultCode.SERVICE_UNAVAILABLE.getMessage())
            );
        }


    }

    @Nested
    @DisplayName("枚举完整性测试")
    class EnumIntegrityTests {

        @ParameterizedTest
        @EnumSource(ResultCode.class)
        @DisplayName("所有状态码都有有效的code和message")
        void testAllResultCodesHaveValidCodeAndMessage(ResultCode resultCode) {
            // Then
            assertAll(
                () -> assertNotNull(resultCode.getCode(), "状态码不能为null: " + resultCode),
                () -> assertTrue(resultCode.getCode() >= 200 && resultCode.getCode() < 600, 
                        "状态码应在HTTP范围内: " + resultCode.getCode()),
                () -> assertNotNull(resultCode.getMessage(), "消息不能为null: " + resultCode),
                () -> assertFalse(resultCode.getMessage().trim().isEmpty(), 
                        "消息不能为空: " + resultCode)
            );
        }

        @Test
        @DisplayName("状态码唯一性测试")
        void testCodeUniqueness() {
            // Given
            ResultCode[] values = ResultCode.values();

            // When & Then
            for (int i = 0; i < values.length; i++) {
                for (int j = i + 1; j < values.length; j++) {
                    assertNotEquals(
                        values[i].getCode(), 
                        values[j].getCode(),
                        String.format("状态码重复: %s 和 %s 都使用了 %d", 
                            values[i], values[j], values[i].getCode())
                    );
                }
            }
        }

        @Test
        @DisplayName("枚举数量测试")
        void testEnumCount() {
            // When
            ResultCode[] values = ResultCode.values();

            // Then
            assertEquals(9, values.length, "ResultCode枚举数量应该为9个");
        }
    }

    @Nested
    @DisplayName("HTTP状态码分类测试")
    class HttpStatusCategoryTests {

        @Test
        @DisplayName("2xx成功状态码测试")
        void testSuccessStatusCodes() {
            assertTrue(ResultCode.SUCCESS.getCode() >= 200 && ResultCode.SUCCESS.getCode() < 300);
        }

        @Test
        @DisplayName("4xx客户端错误状态码测试")
        void testClientErrorStatusCodes() {
            assertAll(
                () -> assertTrue(ResultCode.FAILURE.getCode() >= 400 && ResultCode.FAILURE.getCode() < 500),
                () -> assertTrue(ResultCode.PARAM_ERROR.getCode() >= 400 && ResultCode.PARAM_ERROR.getCode() < 500),
                () -> assertTrue(ResultCode.DATA_NOT_FOUND.getCode() >= 400 && ResultCode.DATA_NOT_FOUND.getCode() < 500),
                () -> assertTrue(ResultCode.DATA_EXISTS.getCode() >= 400 && ResultCode.DATA_EXISTS.getCode() < 500),
                () -> assertTrue(ResultCode.UNAUTHORIZED.getCode() >= 400 && ResultCode.UNAUTHORIZED.getCode() < 500),
                () -> assertTrue(ResultCode.FORBIDDEN.getCode() >= 400 && ResultCode.FORBIDDEN.getCode() < 500),
                () -> assertTrue(ResultCode.PARAM_ERROR.getCode() >= 400 && ResultCode.PARAM_ERROR.getCode() < 500)
            );
        }

        @Test
        @DisplayName("5xx服务器错误状态码测试")
        void testServerErrorStatusCodes() {
            assertAll(
                () -> assertTrue(ResultCode.INTERNAL_SERVER_ERROR.getCode() >= 500 && ResultCode.INTERNAL_SERVER_ERROR.getCode() < 600),
                () -> assertTrue(ResultCode.SERVICE_UNAVAILABLE.getCode() >= 500 && ResultCode.SERVICE_UNAVAILABLE.getCode() < 600)
            );
        }
    }
}
