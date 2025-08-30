package com.kinkle.helloquick.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 * <p>
 * 用于包装分页查询的响应结果，遵循用户偏好的分页逻辑要求。
 * 适用于所有需要分页的列表类型API端点。
 * </p>
 *
 * @param <T> 数据类型
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private int currentPage;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private long totalCount;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 当前页数据
     */
    private List<T> records;

    /**
     * 是否有上一页
     */
    private boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 创建分页结果
     *
     * @param records     当前页数据
     * @param currentPage 当前页码
     * @param pageSize    每页大小
     * @param totalCount  总记录数
     * @param <T>         数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, int currentPage, int pageSize, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        return PageResult.<T>builder()
                .records(records)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .hasPrevious(currentPage > 1)
                .hasNext(currentPage < totalPages)
                .build();
    }

    /**
     * 创建空的分页结果
     *
     * @param currentPage 当前页码
     * @param pageSize    每页大小
     * @param <T>         数据类型
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty(int currentPage, int pageSize) {
        return PageResult.<T>builder()
                .records(List.of())
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalCount(0L)
                .totalPages(0)
                .hasPrevious(false)
                .hasNext(false)
                .build();
    }

    /**
     * 获取开始索引（用于数据库查询）
     *
     * @return 开始索引
     */
    public int getOffset() {
        return (currentPage - 1) * pageSize;
    }
}
