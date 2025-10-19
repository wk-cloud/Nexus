package com.nexus.common.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * iService Plus
 * T 是 实体 类
 * V 是 vo 类
 * @author wk
 * @date 2025/04/08
 */
public interface IServicePlus<T, V> extends IService<T> {

    /**
     * 当前 vo 类
     *
     * @return {@link Class }<{@link V }>
     */
    default Class<V> currentVoClass() {
        return (Class<V>) ReflectionKit.getSuperClassGenericType(this.getClass(), IServicePlus.class, 1);
    }

    /**
     * 根据 id 查询 vo
     *
     * @param id id
     * @return {@link V }
     */
    default V queryVoById(Serializable id) {
        return queryVoById(id, this.currentVoClass());
    }

    /**
     * 根据 id 查询 vo
     *
     * @param id      id
     * @param voClass vo 类
     * @return {@link C }
     */
    default <C> C queryVoById(Serializable id, Class<C> voClass) {
        T t = getById(id);
        return ObjectUtils.isNull(t) ? null : BeanUtils.toBean(t, voClass);
    }

    /**
     * 根据 id 查询 vo 列表
     *
     * @param ids id 列表
     * @return {@link List }<{@link V }>
     */
    default List<V> queryVoListByIds(Collection<? extends Serializable> ids) {
        return queryVoListByIds(ids, this.currentVoClass());
    }

    /**
     * 根据 id 查询 vo 列表
     *
     * @param ids     id 列表
     * @param voClass vo 类
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoListByIds(Collection<? extends Serializable> ids, Class<C> voClass) {
        List<T> list = listByIds(ids);
        return CollectionUtils.isEmpty(list) ? CollectionUtils.emptyList() : BeanUtils.copyToList(list, voClass);
    }

    /**
     * 查询 vo 列表（根据 columnMap 条件）
     *
     * @param map 查询条件
     * @return {@link List }<{@link V }>
     */
    default List<V> queryVoListByMap(Map<String, Object> map) {
        // 根据columnMap条件查询vo列表
        return queryVoListByMap(map, this.currentVoClass());
    }

    /**
     * 查询 vo 列表（根据 columnMap 条件）
     *
     * @param map 查询条件
     * @param voClass vo类
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoListByMap(Map<String, Object> map, Class<C> voClass) {
        List<T> list = this.listByMap(map);
        return CollectionUtils.isEmpty(list) ? CollectionUtils.emptyList() : BeanUtils.copyToList(list, voClass);
    }


    /**
     * 根据 wrapper 查询 vo
     *
     * @param wrapper 查询包装器
     * @return {@link V }
     */
    default V queryVoOne(Wrapper<T> wrapper) {
        return queryVoOne(wrapper, this.currentVoClass());
    }

    /**
     * 根据 wrapper 查询 vo
     *
     * @param wrapper 查询包装器
     * @param voClass vo 类
     * @return {@link C }
     */
    default <C> C queryVoOne(Wrapper<T> wrapper, Class<C> voClass) {
        T obj = this.getOne(wrapper);
        return ObjectUtils.isNull(obj) ? null : BeanUtils.toBean(obj, voClass);
    }

    /**
     * 查询 vo 列表
     *
     * @param wrapper 查询包装器
     * @return {@link List }<{@link V }>
     */
    default List<V> queryVoList(Wrapper<T> wrapper) {
        return queryVoList(wrapper, this.currentVoClass());
    }

    /**
     * 查询 vo 列表
     *
     * @param wrapper 查询包装器
     * @param voClass vo 类
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoList(Wrapper<T> wrapper, Class<C> voClass) {
        List<T> list = this.list(wrapper);
        return CollectionUtils.isEmpty(list) ? CollectionUtils.emptyList() : BeanUtils.copyToList(list, voClass);
    }

    /**
     * 查询 vo 列表
     *
     * @return {@link List }<{@link V }>
     */
    default List<V> queryVoList() {
        return queryVoList(this.currentVoClass());
    }

    /**
     * 查询 vo 列表
     *
     * @param voClass vo类
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoList(Class<C> voClass) {
        List<T> list = list();
        return CollectionUtils.isEmpty(list) ? CollectionUtils.emptyList() : BeanUtils.copyToList(list, voClass);
    }

    /**
     * 分页查询 vo
     *
     * @param page    分页
     * @param wrapper 查询包装器
     * @return {@link P }
     */
    default <P extends IPage<V>> P queryVoPage(IPage<T> page, Wrapper<T> wrapper) {
        return queryVoPage(page, wrapper, this.currentVoClass());
    }

    /**
     * 分页查询 vo
     *
     * @param page    分页
     * @param wrapper 查询包装器
     * @param voClass vo 类
     * @return {@link P }
     */
    default <C,P extends IPage<C>> P queryVoPage(IPage<T> page, Wrapper<T> wrapper, Class<C> voClass) {
        return (P) this.page(page, wrapper).convert(t -> BeanUtils.toBean(t, voClass));
    }

}
