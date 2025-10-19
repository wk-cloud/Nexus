package com.nexus.common.utils;


import cn.hutool.core.util.ObjectUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对象工具类
 * @author wk
 * @date 2025/09/14
 */
public class ObjectUtils extends ObjectUtil {

    /**
     * 不为空
     *
     * @param object 对象
     * @return boolean
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * 不为空
     *
     * @param object 对象
     * @return boolean
     */
    public static boolean isNotEmpty(Object[] object) {
        return !isEmpty(object);
    }

    /**
     * 转换成 list 集合
     *
     * @param obj   对象
     * @param clazz clazz
     * @return {@link List }<{@link T }>
     */
    public static <T> List<T> toList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 转换成 set 集合
     *
     * @param obj   对象
     * @param clazz clazz
     * @return {@link Set }<{@link T }>
     */
    public static <T> Set<T> toSet(Object obj, Class<T> clazz) {
        Set<T> result = new HashSet<>();
        if (obj instanceof Set<?>) {
            for (Object o : (Set<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
