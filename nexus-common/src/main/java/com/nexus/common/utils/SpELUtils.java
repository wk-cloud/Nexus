package com.nexus.common.utils;


import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.regex.Pattern;

/**
 * SpEL 工具
 *
 * @author wk
 * @date 2025/07/31
 */
public class SpELUtils {

    /**
     *  白名单正则：只允许数字、运算符和括号
     * */
    private static final Pattern SAFE_PATTERN =
            Pattern.compile("^[\\d\\s+\\-*/().]*$");

    /**
     * 计算 int 值
     * @param expression 表达
     * @return double
     */
    public static int evaluateInt(String expression) {
        if (!SAFE_PATTERN.matcher(expression).matches()) {
            throw new IllegalArgumentException("非法表达式");
        }

        ExpressionParser parser = new SpelExpressionParser(
                new SpelParserConfiguration(SpelCompilerMode.OFF, null)
        );
        return parser.parseExpression(expression)
                .getValue(Integer.class);
    }

    /**
     * 计算 double 值
     * @param expression 表达
     * @return double
     */
    public static double evaluateDouble(String expression) {
        if (!SAFE_PATTERN.matcher(expression).matches()) {
            throw new IllegalArgumentException("非法表达式");
        }
        ExpressionParser parser = new SpelExpressionParser(
                new SpelParserConfiguration(SpelCompilerMode.OFF, null)
        );
        return parser.parseExpression(expression)
                .getValue(Double.class);
    }
}
