package com.nexus.common.utils;


import com.nexus.common.core.base.TreeEntity;
import org.apache.poi.ss.formula.functions.T;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 树形工具类（将数据封装成树形结构）
 *
 * @author wk
 * @date 2022/9/27
 */
public class TreeUtils {

    /**
     * 创建树形结构数据
     *
     * @param dataList 数据列表
     * @return {@link List }<{@link T }>
     */
    public static <T extends TreeEntity<T>> List<T> createTree(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        // 查找出所有的一级节点
        return dataList
                .stream()
                .filter(item -> item.getParentId().equals(0L))
                .peek(item -> {
                    item.setLevel(1);
                    item.setChildren(getTreeChildren(item, dataList));
                })
                .sorted(Comparator.comparing(item -> (item.getSort() == null) ? 0 : item.getSort()))
                .collect(Collectors.toList());
    }

    /**
     * 选择子节点
     *
     * @param rootTree 顶级树节点
     * @param dataList 数据列表
     * @return {@link List }<{@link T }>
     */
    private static <T extends TreeEntity<T>> List<T> getTreeChildren(T rootTree, List<T> dataList) {
        return dataList
                .stream()
                .filter(permission -> permission.getParentId().equals(rootTree.getId()))
                .peek(item -> {
                    item.setLevel(rootTree.getLevel() + 1);
                    item.setChildren(getTreeChildren(item, dataList));
                })
                .sorted(Comparator.comparing(item -> (item.getSort() == null) ? 0 : item.getSort()))
                .collect(Collectors.toList());
    }
}
