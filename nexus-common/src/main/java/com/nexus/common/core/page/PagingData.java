package com.nexus.common.core.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.common.utils.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页数据
 *
 * @author wk
 * @date 2024/03/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PagingData<T> implements Serializable {

    /**
     * 数据列表
     */
    private List<T> dataList = new ArrayList<>();

    /**
     * 数据总量
     */
    private Long total = 0L;

    /**
     * 有下一页
     */
    private boolean hasNext;

    /**
     * 有上一页
     */
    private boolean hasPrevious;

    public PagingData(Page<T> page) {
        this.dataList = page.getRecords();
        this.total = page.getTotal();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

    /**
     * 构造分页数据
     *
     * @param page 分页信息
     * @return {@link PagingData}<{@link T}>
     */
    public static <T> PagingData<T> build(Page<T> page) {
        return new PagingData<>(page);
    }

    /**
     * 构造分页数据
     *
     * @param page 分页信息
     * @return {@link PagingData}<{@link V}>
     */
    public static <V, T> PagingData<V> build(Page<T> page, Class<V> clazz) {
        Page<V> newPage = new Page<>(page.getCurrent(), page.getSize());
        newPage.setTotal(page.getTotal());
        newPage.setRecords(BeanUtils.copyToList(page.getRecords(), clazz));
        return new PagingData<>(newPage);
    }

    /**
     * 构造分页数据
     *
     * @param dataList    数据列表
     * @param total       总数量
     * @param hasNext     是否有下一页
     * @param hasPrevious 是否有上一页
     * @return {@link PagingData}<{@link T}>
     */
    public static <T> PagingData<T> build(List<T> dataList, Long total, boolean hasNext, boolean hasPrevious) {
        return new PagingData<>(dataList, total, hasNext, hasPrevious);
    }

    /**
     * 构建分页数据
     *
     * @param pageNumber 当前页面
     * @param pageSize   页面大小
     * @param dataList   数据列表
     * @return {@link PagingData}<{@link T}>
     */
    public static <T> PagingData<T> build(List<T> dataList, Integer pageNumber, Integer pageSize) {
        return new PagingData<>(createPage(dataList, pageNumber, pageSize));
    }

    /**
     * 构建分页数据
     *
     * @param pageNumber 当前页面
     * @param total      总记录数
     * @param pageSize   页面大小
     * @param dataList   数据列表
     * @return {@link PagingData}<{@link T}>
     */
    public static <T> PagingData<T> build(List<T> dataList, Integer total, Integer pageNumber, Integer pageSize) {
        return new PagingData<>(createPage(dataList, total, pageNumber, pageSize));
    }

    /**
     * 创建分页数据
     *
     * @param pageNumber 当前页面
     * @param total      总记录数
     * @param pageSize   页面大小
     * @param dataList   数据列表
     * @return {@link Page}
     */
    public static <T> Page<T> createPage(List<T> dataList, Integer total, Integer pageNumber, Integer pageSize) {
        Page<T> page = new Page<>();
        page.setCurrent(pageNumber).setSize(pageSize).setTotal(total).setRecords(dataList);
        return page;
    }


    /**
     * 创建分页数据
     *
     * @param pageNumber 当前页面
     * @param pageSize   页面大小
     * @param dataList   数据列表
     * @return {@link Page}
     */
    public static <T> Page<T> createPage(List<T> dataList, Integer pageNumber, Integer pageSize) {
        Page<T> page = new Page<>();
        if (dataList.isEmpty()) {
            page.setCurrent(pageNumber).setSize(pageSize).setTotal(dataList.size()).setRecords(new ArrayList<>());
            return page;
        }

        int size = dataList.size();

        if (pageSize > size) {
            pageSize = size;
        }

        // 求出最大页数，防止pageNumber越界
        int maxPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;

        if (pageNumber > maxPage) {
            pageNumber = maxPage;
        }

        // 当前页第一条数据的下标
        int curIdx = pageNumber > 1 ? (pageNumber - 1) * pageSize : 0;

        List<T> pageList = new ArrayList<>();

        // 将当前页的数据放进pageList
        for (int i = 0; i < pageSize && curIdx + i < size; i++) {
            pageList.add(dataList.get(curIdx + i));
        }

        page.setCurrent(pageNumber).setSize(pageSize).setTotal(dataList.size()).setRecords(pageList);
        return page;
    }
}
