package com.nexus.common.core.helper;



import com.nexus.common.core.sensitive.SensitiveWord;

import java.util.Set;

/**
 * 敏感词助手
 *
 * @author wk
 * @date 2025/07/13
 */
public class SensitiveWordHelper {

    /**
     * 敏感词
     */
    private static final SensitiveWord sensitiveWord = SensitiveWord.newInstance().init();

    /**
     * 替换敏感词
     *
     * @param text 文本
     * @return {@link String }
     */
    public static String replace(String text) {
        return sensitiveWord.replace(text);
    }

    /**
     * 替换敏感词
     *
     * @param text       文本
     * @param replaceStr 替换字符串
     * @return {@link String }
     */
    public static String replace(String text, String replaceStr) {
        return sensitiveWord.replace(text, replaceStr);
    }

    /**
     * 是否包含敏感词
     *
     * @param text 文本
     * @return boolean
     */
    public static boolean contains(String text) {
        return sensitiveWord.contains(text);
    }

    /**
     * 返回匹配到的第一个敏感词
     *
     * @param text 文本
     * @return {@link String }
     */
    public static String findFirst(String text) {
        return sensitiveWord.findFirst(text);
    }

    /**
     * 返回匹配到的所有敏感词
     *
     * @param text 文本
     * @return {@link Set }<{@link String }>
     */
    public static Set<String> findAll(String text) {
        return sensitiveWord.findAll(text);
    }

}
