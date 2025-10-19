package com.nexus.common.core.rule;

import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.spel.SpELRule;

/**
 * MVEL 规则实例
 *
 * @author wk
 * @date 2025/09/15
 */
public class MVELRules {

    private MVELRules() {}

    /**
     * 创建 MVELRule 实例
     * @return {@link SpELRule }
     */
    public static MVELRule defaults(){
        return new MVELRule();
    }
}
