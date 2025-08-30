package com.kinkle.helloquick.common.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult类单元测试
 * <p>
 * 测试覆盖率目标：95%+
 * 遵循pr-review.mdc的测试质量标准。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("PageResult 分页响应结果类测试")
class PageResultTest {

    @Nested
    @DisplayName("基本功能测试")
    class BasicTests {

        @Test
        @DisplayName("正常分页数据创建")
        void testOfWithValidData() {
            // Given
            List<String> records = Arrays.asList("item1", "item2", "item3");
            int currentPage = 1;
            int pageSize = 10;
            long totalCount = 25;

            // When
            PageResult<String> result = PageResult.of(records, currentPage, pageSize, totalCount);

            // Then
            assertAll(
                () -> assertEquals(records, result.getRecords()),
                () -> assertEquals(currentPage, result.getCurrentPage()),
                () -> assertEquals(pageSize, result.getPageSize()),
                () -> assertEquals(totalCount, result.getTotalCount()),
                () -> assertEquals(3, result.getTotalPages()), // Math.ceil(25/10) = 3
                () -> assertFalse(result.isHasPrevious()),
                () -> assertTrue(result.isHasNext())
            );
        }

        @Test
        @DisplayName("最后一页数据测试")
        void testLastPage() {
            // Given
            List<String> records = Arrays.asList("item1", "item2");
            int currentPage = 3;
            int pageSize = 10;
            long totalCount = 25;

            // When
            PageResult<String> result = PageResult.of(records, currentPage, pageSize, totalCount);

            // Then
            assertAll(
                () -> assertEquals(3, result.getTotalPages()),
                () -> assertTrue(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }

        @Test
        @DisplayName("中间页数据测试")
        void testMiddlePage() {
            // Given
            List<String> records = Arrays.asList("item1", "item2", "item3");
            int currentPage = 2;
            int pageSize = 5;
            long totalCount = 20;

            // When
            PageResult<String> result = PageResult.of(records, currentPage, pageSize, totalCount);

            // Then
            assertAll(
                () -> assertEquals(4, result.getTotalPages()), // Math.ceil(20/5) = 4
                () -> assertTrue(result.isHasPrevious()),
                () -> assertTrue(result.isHasNext())
            );
        }

        @Test
        @DisplayName("单页数据测试")
        void testSinglePage() {
            // Given
            List<String> records = Arrays.asList("item1", "item2");
            int currentPage = 1;
            int pageSize = 10;
            long totalCount = 2;

            // When
            PageResult<String> result = PageResult.of(records, currentPage, pageSize, totalCount);

            // Then
            assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertFalse(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }
    }

    @Nested
    @DisplayName("空数据测试")
    class EmptyDataTests {

        @Test
        @DisplayName("empty方法测试")
        void testEmpty() {
            // Given
            int currentPage = 1;
            int pageSize = 10;

            // When
            PageResult<String> result = PageResult.empty(currentPage, pageSize);

            // Then
            assertAll(
                () -> assertNotNull(result.getRecords()),
                () -> assertTrue(result.getRecords().isEmpty()),
                () -> assertEquals(currentPage, result.getCurrentPage()),
                () -> assertEquals(pageSize, result.getPageSize()),
                () -> assertEquals(0L, result.getTotalCount()),
                () -> assertEquals(0, result.getTotalPages()),
                () -> assertFalse(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }

        @Test
        @DisplayName("空记录列表测试")
        void testWithEmptyRecords() {
            // Given
            List<String> emptyRecords = Collections.emptyList();

            // When
            PageResult<String> result = PageResult.of(emptyRecords, 1, 10, 0);

            // Then
            assertAll(
                () -> assertTrue(result.getRecords().isEmpty()),
                () -> assertEquals(0, result.getTotalPages()),
                () -> assertFalse(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("总数为0的分页测试")
        void testWithZeroTotalCount() {
            // Given
            List<String> records = Collections.emptyList();

            // When
            PageResult<String> result = PageResult.of(records, 1, 10, 0);

            // Then
            assertAll(
                () -> assertEquals(0, result.getTotalPages()),
                () -> assertFalse(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }

        @Test
        @DisplayName("页面大小为1的分页测试")
        void testWithPageSizeOne() {
            // Given
            List<String> records = Arrays.asList("item1");

            // When
            PageResult<String> result = PageResult.of(records, 2, 1, 5);

            // Then
            assertAll(
                () -> assertEquals(5, result.getTotalPages()),
                () -> assertTrue(result.isHasPrevious()),
                () -> assertTrue(result.isHasNext())
            );
        }

        @Test
        @DisplayName("总数等于页面大小的测试")
        void testTotalCountEqualsPageSize() {
            // Given
            List<String> records = Arrays.asList("item1", "item2", "item3");

            // When
            PageResult<String> result = PageResult.of(records, 1, 3, 3);

            // Then
            assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertFalse(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }

        @Test
        @DisplayName("大页码测试")
        void testLargePageNumber() {
            // Given
            List<String> records = Arrays.asList("item1");

            // When
            PageResult<String> result = PageResult.of(records, 100, 10, 1000);

            // Then
            assertAll(
                () -> assertEquals(100, result.getTotalPages()),
                () -> assertTrue(result.isHasPrevious()),
                () -> assertFalse(result.isHasNext())
            );
        }
    }

    @Nested
    @DisplayName("工具方法测试")
    class UtilityTests {

        @Test
        @DisplayName("getOffset方法测试")
        void testGetOffset() {
            // Given & When & Then
            assertEquals(0, PageResult.of(Collections.emptyList(), 1, 10, 0).getOffset());
            assertEquals(10, PageResult.of(Collections.emptyList(), 2, 10, 20).getOffset());
            assertEquals(20, PageResult.of(Collections.emptyList(), 3, 10, 30).getOffset());
            assertEquals(50, PageResult.of(Collections.emptyList(), 6, 10, 100).getOffset());
        }

        @Test
        @DisplayName("Builder模式测试")
        void testBuilderPattern() {
            // Given
            List<String> records = Arrays.asList("test");

            // When
            PageResult<String> result = PageResult.<String>builder()
                    .records(records)
                    .currentPage(2)
                    .pageSize(5)
                    .totalCount(15)
                    .totalPages(3)
                    .hasPrevious(true)
                    .hasNext(true)
                    .build();

            // Then
            assertAll(
                () -> assertEquals(records, result.getRecords()),
                () -> assertEquals(2, result.getCurrentPage()),
                () -> assertEquals(5, result.getPageSize()),
                () -> assertEquals(15, result.getTotalCount()),
                () -> assertEquals(3, result.getTotalPages()),
                () -> assertTrue(result.isHasPrevious()),
                () -> assertTrue(result.isHasNext()),
                () -> assertEquals(5, result.getOffset())
            );
        }
    }

    @Nested
    @DisplayName("数学计算测试")
    class MathCalculationTests {

        @Test
        @DisplayName("总页数计算测试")
        void testTotalPagesCalculation() {
            // 测试各种总页数计算场景
            assertAll(
                // 整除情况
                () -> assertEquals(5, PageResult.of(Collections.emptyList(), 1, 10, 50).getTotalPages()),
                // 有余数情况
                () -> assertEquals(6, PageResult.of(Collections.emptyList(), 1, 10, 51).getTotalPages()),
                // 总数小于页面大小
                () -> assertEquals(1, PageResult.of(Collections.emptyList(), 1, 10, 5).getTotalPages()),
                // 总数为0
                () -> assertEquals(0, PageResult.of(Collections.emptyList(), 1, 10, 0).getTotalPages())
            );
        }

        @Test
        @DisplayName("hasPrevious逻辑测试")
        void testHasPreviousLogic() {
            assertAll(
                () -> assertFalse(PageResult.of(Collections.emptyList(), 1, 10, 100).isHasPrevious()),
                () -> assertTrue(PageResult.of(Collections.emptyList(), 2, 10, 100).isHasPrevious()),
                () -> assertTrue(PageResult.of(Collections.emptyList(), 10, 10, 100).isHasPrevious())
            );
        }

        @Test
        @DisplayName("hasNext逻辑测试")
        void testHasNextLogic() {
            assertAll(
                () -> assertTrue(PageResult.of(Collections.emptyList(), 1, 10, 100).isHasNext()),
                () -> assertTrue(PageResult.of(Collections.emptyList(), 9, 10, 100).isHasNext()),
                () -> assertFalse(PageResult.of(Collections.emptyList(), 10, 10, 100).isHasNext())
            );
        }
    }
}
