package com.nexus.common.utils;


import java.util.*;

/**
 * 集合工具类
 *
 * @author wk
 * @date 2023/04/16
 */
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    /**
     * 得到初始容量
     *
     * @param value 指定容量
     * @return double
     */
    public static int getInitialCapacity(int value){
        return (int)(value / 0.75) + 1;
    }

    /**
     * 不为空
     *
     * @param collection 集合
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> collection){
        return !isEmpty(collection);
    }

    /**
     * 不为空
     *
     * @param  map 集合
     * @return boolean
     */
    public static boolean isNotEmpty(Map<?,?> map){
        return !isEmpty(map);
    }

    /**
     * 空 List
     *
     * @return {@link List }
     */
    public static List emptyList(){
        return Collections.emptyList();
    }

    /**
     * 空 Set
     *
     * @return {@link Set }
     */
    public static Set emptySet(){
        return Collections.emptySet();
    }

    /**
     * 空 Map
     *
     * @return {@link Map }
     */
    public static Map emptyMap() {
        return Collections.emptyMap();
    }

}
