package com.nexus.common.core.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * 树实体
 *
 * @author wk
 * @date 2025/09/14
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TreeEntity<T> {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 父级id
     */
    private Long parentId;

    /**
     * 树层级
     */
    private Integer level = 1;

    /**
     * 排序
     */
    private Integer sort = 1;

    /**
     * 子级数据列表
     */
    private List<T> children = new ArrayList<>();
}
