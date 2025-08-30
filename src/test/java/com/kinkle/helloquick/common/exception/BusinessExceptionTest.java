package com.kinkle.helloquick.common.exception;

import com.kinkle.helloquick.common.result.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * BusinessException单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("BusinessException单元测试")
class BusinessExceptionTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该正确创建带ResultCode的异常")
        void shouldCreateExceptionWithResultCode() {
            // Given
            ResultCode resultCode = ResultCode.DATA_NOT_FOUND;

            // When
            BusinessException exception = new BusinessException(resultCode);

            // Then
            assertThat(exception.getResultCode()).isEqualTo(resultCode);
            assertThat(exception.getMessage()).isEqualTo(resultCode.getMessage());
        }

        @Test
        @DisplayName("应该正确创建带自定义消息的异常")
        void shouldCreateExceptionWithCustomMessage() {
            // Given
            ResultCode resultCode = ResultCode.DATA_NOT_FOUND;
            String customMessage = "用户不存在";

            // When
            BusinessException exception = new BusinessException(resultCode, customMessage);

            // Then
            assertThat(exception.getResultCode()).isEqualTo(resultCode);
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("应该正确创建带原因的异常")
        void shouldCreateExceptionWithCause() {
            // Given
            ResultCode resultCode = ResultCode.INTERNAL_SERVER_ERROR;
            String message = "数据库连接失败";
            Throwable cause = new RuntimeException("Connection timeout");

            // When
            BusinessException exception = new BusinessException(resultCode, message, cause);

            // Then
            assertThat(exception.getResultCode()).isEqualTo(resultCode);
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("静态工厂方法测试")
    class StaticFactoryMethodTests {

        @Test
        @DisplayName("应该正确创建数据不存在异常")
        void shouldCreateDataNotFoundExceptionCorrectly() {
            // Given
            String entityName = "用户";

            // When
            BusinessException exception = BusinessException.dataNotFound(entityName);

            // Then
            assertThat(exception.getResultCode()).isEqualTo(ResultCode.DATA_NOT_FOUND);
            assertThat(exception.getMessage()).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("应该正确创建数据已存在异常")
        void shouldCreateDataExistsExceptionCorrectly() {
            // Given
            String entityName = "用户名";

            // When
            BusinessException exception = BusinessException.dataExists(entityName);

            // Then
            assertThat(exception.getResultCode()).isEqualTo(ResultCode.DATA_EXISTS);
            assertThat(exception.getMessage()).isEqualTo("用户名已存在");
        }

        @Test
        @DisplayName("应该正确创建参数错误异常")
        void shouldCreateParamErrorExceptionCorrectly() {
            // Given
            String message = "用户名不能为空";

            // When
            BusinessException exception = BusinessException.paramError(message);

            // Then
            assertThat(exception.getResultCode()).isEqualTo(ResultCode.PARAM_ERROR);
            assertThat(exception.getMessage()).isEqualTo(message);
        }

        @Test
        @DisplayName("应该正确创建未授权异常")
        void shouldCreateUnauthorizedExceptionCorrectly() {
            // When
            BusinessException exception = BusinessException.unauthorized();

            // Then
            assertThat(exception.getResultCode()).isEqualTo(ResultCode.UNAUTHORIZED);
            assertThat(exception.getMessage()).isEqualTo(ResultCode.UNAUTHORIZED.getMessage());
        }

        @Test
        @DisplayName("应该正确创建禁止访问异常")
        void shouldCreateForbiddenExceptionCorrectly() {
            // When
            BusinessException exception = BusinessException.forbidden();

            // Then
            assertThat(exception.getResultCode()).isEqualTo(ResultCode.FORBIDDEN);
            assertThat(exception.getMessage()).isEqualTo(ResultCode.FORBIDDEN.getMessage());
        }
    }

    @Nested
    @DisplayName("异常继承测试")
    class InheritanceTests {

        @Test
        @DisplayName("应该是RuntimeException的子类")
        void shouldBeSubclassOfRuntimeException() {
            // Given
            BusinessException exception = BusinessException.dataNotFound("测试");

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该可以被抛出和捕获")
        void shouldBeThrowableAndCatchable() {
            // Given & When & Then
            BusinessException thrownException = assertThrows(BusinessException.class, () -> {
                throw BusinessException.dataNotFound("用户");
            });

            assertThat(thrownException.getResultCode()).isEqualTo(ResultCode.DATA_NOT_FOUND);
            assertThat(thrownException.getMessage()).isEqualTo("用户不存在");
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理空字符串实体名称")
        void shouldHandleEmptyEntityName() {
            // Given
            String emptyEntityName = "";

            // When
            BusinessException exception = BusinessException.dataNotFound(emptyEntityName);

            // Then
            assertThat(exception.getMessage()).isEqualTo("不存在");
        }

        @Test
        @DisplayName("应该处理null实体名称")
        void shouldHandleNullEntityName() {
            // Given
            String nullEntityName = null;

            // When
            BusinessException exception = BusinessException.dataExists(nullEntityName);

            // Then
            assertThat(exception.getMessage()).isEqualTo("null已存在");
        }

        @Test
        @DisplayName("应该处理特殊字符实体名称")
        void shouldHandleSpecialCharacterEntityName() {
            // Given
            String specialEntityName = "用户@#$%";

            // When
            BusinessException exception = BusinessException.dataNotFound(specialEntityName);

            // Then
            assertThat(exception.getMessage()).isEqualTo("用户@#$%不存在");
        }
    }

    @Nested
    @DisplayName("序列化测试")
    class SerializationTests {

        @Test
        @DisplayName("应该有正确的serialVersionUID")
        void shouldHaveCorrectSerialVersionUID() {
            // Given
            BusinessException exception = BusinessException.dataNotFound("测试");

            // Then
            // 验证异常可以被序列化（通过创建实例验证）
            assertThat(exception).isNotNull();
            assertThat(exception.getClass().getDeclaredFields())
                    .anyMatch(field -> field.getName().equals("serialVersionUID"));
        }
    }
}

