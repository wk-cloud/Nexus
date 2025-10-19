package com.nexus.common.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author wk
 * @date 2023/12/13
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final String EMPTY_STR = "";

    /**
     * 空字符串
     *
     * @return {@link String}
     */
    public static String emptyStr(){
        return EMPTY_STR;
    }

    /**
     * 字符串匹配提取
     *
     * @param str 待提取字符串
     * @param reg 正则表达式
     * @return {@link List}<{@link Map}<{@link String},{@link String}>>
     */
    public static List<Map<String,String>> match(String str, String reg){
        ArrayList<Map<String,String>> resultList = new ArrayList<>();
        if(StringUtils.isNotBlank(str)){
            Pattern compile = Pattern.compile(reg);
            Matcher matcher = compile.matcher(str);
            while (matcher.find()){
                HashMap<String, String> map = new HashMap<>();
                if(matcher.groupCount() > 0){
                    for(int i = 1;i <= matcher.groupCount(); i++){
                        map.put(Integer.toString(i),matcher.group(i));
                    }
                }
                map.put("0",matcher.group());
                map.put("input",str);
                resultList.add(map);
            }
        }
        return resultList;
    }

}
