package com.nexus.common.core.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基础映射器
 * T 是 实体 类
 * V 是 vo 类
 *
 * @author wk
 * @date 2025/04/07
 */
public interface BaseMapperPlus<T, V> extends BaseMapper<T> {

    /**
     * 当前 vo 类
     *
     * @return {@link Class }<{@link V }>
     */
    default Class<V> currentVoClass() {
        return (Class<V>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 1);
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
     * @param voClass vo类
     * @return {@link C }
     */
    default <C> C queryVoById(Serializable id, Class<C> voClass) {
        T obj = this.selectById(id);
        return ObjectUtils.isNull(obj) ? null : BeanUtils.toBean(obj, voClass);
    }

    /**
     * 根据 id 查询 vo 列表
     *
     * @param idList id列表
     * @return {@link List }<{@link V }>
     */
    default List<V> queryVoListByIds(Collection<? extends Serializable> idList) {
        return queryVoListByIds(idList, this.currentVoClass());
    }

    /**
     * 根据 id 查询 vo 列表
     *
     * @param idList  id列表
     * @param voClass vo类
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoListByIds(Collection<? extends Serializable> idList, Class<C> voClass) {
        List<T> list = this.selectBatchIds(idList);
        return CollectionUtils.isEmpty(list) ? CollectionUtils.emptyList() : BeanUtils.copyToList(list, voClass);
    }

    /**
     * 查询 vo 列表（根据 columnMap 条件）
     *
     * @param map
     * @return {@link List }<{@link V }>
     */
    default List<V> queryVoListByMap(Map<String, Object> map) {
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
        List<T> list = this.selectByMap(map);
        return CollectionUtils.isEmpty(list) ? CollectionUtils.emptyList() : BeanUtils.copyToList(list, voClass);
    }

    /**
     * 查询一条 vo 记录
     *
     * @param wrapper 查询包装器
     * @return {@link V }
     */
    default V queryVoOne(Wrapper<T> wrapper) {
        return queryVoOne(wrapper, this.currentVoClass());
    }

    /**
     * 查询一条 vo 记录
     *
     * @param wrapper 查询包装器
     * @param voClass vo类
     * @return {@link C }
     */
    default <C> C queryVoOne(Wrapper<T> wrapper, Class<C> voClass) {
        T obj = this.selectOne(wrapper);
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
     * @param wrapper 封套
     * @param voClass vo类
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoList(Wrapper<T> wrapper, Class<C> voClass) {
        List<T> list = this.selectList(wrapper);
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
     * @return {@link List }<{@link C }>
     */
    default <C> List<C> queryVoList(Class<C> voClass) {
        List<T> list = selectList(new LambdaQueryWrapper<>());
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
     * @param voClass vo类
     * @return {@link P }
     */
    default <C, P extends IPage<C>> P queryVoPage(IPage<T> page, Wrapper<T> wrapper, Class<C> voClass) {
        return (P) this.selectPage(page, wrapper).convert(t -> BeanUtils.toBean(t, voClass));
    }

}
