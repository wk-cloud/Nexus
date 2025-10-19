package com.nexus.common.utils;

import cn.hutool.http.HtmlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html工具类
 *
 * @author wk
 * @date 2023/05/16
 */
public class HtmlUtils extends HtmlUtil {

    /**
     * 根据正则匹配网页元素
     *
     * @param url url，网页url
     * @param reg 正则表达式
     * @return {@link List}<{@link String}>
     */
    public static List<Map<String,String>> matchHtml(String url, String reg){
        ArrayList<Map<String,String>> resultList = new ArrayList<>();
        String html = HttpUtils.get(url);
        if(StringUtils.isNotBlank(html)){
            Pattern compile = Pattern.compile(reg);
            Matcher matcher = compile.matcher(html);
            while (matcher.find()){
                HashMap<String, String> map = new HashMap<>();
                if(matcher.groupCount() > 0){
                    for(int i = 1;i <= matcher.groupCount(); i++){
                        map.put(Integer.toString(i),matcher.group(i));
                    }
                }
                map.put("0",matcher.group());
                map.put("input",html);
                resultList.add(map);
            }
        }
        return resultList;
    }
}
