package com.nexus.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


/**
 * 随机盐生成工具
 *
 * @author wk
 * @date 2022/7/22
 */
public class RandomSaltUtils {

    /**
     * 创建随机盐
     *
     * @param len 随机盐前缀长度
     * @return {@link String}
     */
    public static String createRandomSalt(int len){
        if(len > 32){
            len = 32;
        }else if(len < 0){
            len = 0;
        }
        long tempDate =LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String uuid = UUID.randomUUID().toString().substring(0, len);
        return uuid + tempDate;
    }

    public static void main(String[] args) {
        System.out.println(createRandomSalt(42));
    }
}
