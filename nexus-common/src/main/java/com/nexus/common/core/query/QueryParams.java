package com.nexus.common.core.query;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.common.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 查询参数
 *
 * @author wk
 * @date 2024/03/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryParams implements Serializable {

    /**
     * 当前记录起始索引 默认值
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 每页显示记录数 默认值 默认查全部
     */
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    /**
     * 页码
     */
    private Integer pageNumber;

    /**
     * 页面大小
     */
    private Integer pageSize;

    /**
     * 偏移量
     */
    private Integer offset;

    public QueryParams(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.offset = (pageNumber - 1) * pageSize;
    }

    /**
     * 构建 Page
     *
     * @return {@link Page }<{@link T }>
     */
    public <T> Page<T> build() {
        Integer pageNumber = ObjectUtils.defaultIfNull(getPageNumber(), DEFAULT_PAGE_NUM);
        Integer pageSize = ObjectUtils.defaultIfNull(getPageSize(), DEFAULT_PAGE_SIZE);
        if (pageNumber <= 0) {
            pageNumber = DEFAULT_PAGE_NUM;
        }
        this.offset = (pageNumber - 1) * pageSize;
        return new Page<>(pageNumber, pageSize);
    }
}
