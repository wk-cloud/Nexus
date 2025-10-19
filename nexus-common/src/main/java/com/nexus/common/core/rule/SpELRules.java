package com.nexus.common.core.rule;

import org.jeasy.rules.spel.SpELRule;

/**
 * SpEL 规则实例
 *
 * @author wk
 * @date 2025/09/15
 */
public class SpELRules {

    private SpELRules() {}

    /**
     * 创建 SpELRule 实例
     * @return {@link SpELRule }
     */
    public static SpELRule defaults(){
        return new SpELRule();
    }
}
