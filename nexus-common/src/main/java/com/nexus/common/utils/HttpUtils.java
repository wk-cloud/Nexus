package com.nexus.common.utils;


import cn.hutool.http.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * http 工具类
 *
 * @author wk
 * @date 2024/10/06
 */
public class HttpUtils extends HttpUtil {

    /**
     * 构建请求 url
     * 支持路径参数和查询参数
     * @param url      请求地址
     * @param paramMap 请求参数映射
     * @return {@link String }
     */
    private static String buildUrl(String url, Map<String, Object> paramMap) {
        if(url == null){
            return null;
        }
        if (paramMap == null || paramMap.isEmpty()) {
            return url;
        }

        // 复制一份参数映射，避免修改原始数据
        Map<String, Object> remainingParams = new HashMap<>(paramMap);
        StringBuilder resultUrl = new StringBuilder();

        // 1. 替换路径中的占位符 {param}
        int startIndex = 0;
        while (true) {
            int startBrace = url.indexOf("{", startIndex);
            int endBrace = (startBrace != -1) ? url.indexOf("}", startBrace) : -1;

            if (startBrace == -1 || endBrace == -1) {
                // 追加剩余未处理的 URL 部分
                resultUrl.append(url, startIndex, url.length());
                break;
            }

            // 追加当前位置到左大括号之前的字符
            resultUrl.append(url, startIndex, startBrace);

            String paramKey = url.substring(startBrace + 1, endBrace);
            if (remainingParams.containsKey(paramKey)) {
                // 替换占位符为实际值
                resultUrl.append(remainingParams.get(paramKey).toString());
                remainingParams.remove(paramKey); // 从剩余参数中移除
            } else {
                // 未提供参数值，保留原始占位符
                resultUrl.append("{").append(paramKey).append("}");
            }

            startIndex = endBrace + 1;
        }

        // 2. 处理剩余的查询参数
        if (!remainingParams.isEmpty()) {
            // 判断是否需要添加 "?" 或 "&"
            char connector = (resultUrl.indexOf("?") == -1) ? '?' : '&';
            resultUrl.append(connector);

            // 拼接查询参数
            for (Map.Entry<String, Object> entry : remainingParams.entrySet()) {
                resultUrl.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            resultUrl.deleteCharAt(resultUrl.length() - 1); // 移除末尾多余的 "&"
        }

        return resultUrl.toString();
    }
}
