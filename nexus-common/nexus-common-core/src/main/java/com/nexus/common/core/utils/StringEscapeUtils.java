package com.nexus.common.core.utils;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 字符串转义工具
 *
 * @author wk
 * @date 2025/02/13
 */
public class StringEscapeUtils {

    private StringEscapeUtils() {
    }

    /**
     * HTML 转义
     *
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml4(String input) {
        if (CharSequenceUtil.isBlank(input)) {
            return input;
        }
        return HtmlUtils.escape(input);
    }

    /**
     * HTML 反转义
     *
     * @param input 输入字符串
     * @return 反转义后的字符串
     */
    public static String unescapeHtml4(String input) {
        if (CharSequenceUtil.isBlank(input)) {
            return input;
        }
        return HtmlUtils.unescape(input);
    }

    /**
     * 清除 HTML 标签（保留文本内容）
     *
     * @param input 输入字符串
     * @return 清除标签后的字符串
     */
    public static String cleanHtmlTag(String input) {
        if (CharSequenceUtil.isBlank(input)) {
            return input;
        }
        return HtmlUtils.cleanHtmlTag(input);
    }
}
