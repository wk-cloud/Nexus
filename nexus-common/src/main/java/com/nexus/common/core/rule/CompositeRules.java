package com.nexus.common.core.rule;

import com.nexus.common.enums.CompositeTypeEnum;
import org.jeasy.rules.support.composite.ActivationRuleGroup;
import org.jeasy.rules.support.composite.CompositeRule;
import org.jeasy.rules.support.composite.ConditionalRuleGroup;
import org.jeasy.rules.support.composite.UnitRuleGroup;

/**
 * 复合规则实例
 *
 * @author wk
 * @date 2025/07/31
 */
public class CompositeRules {

    private CompositeRules() {
    }

    /**
     * 获取复合规则实例
     *
     * @param compositeType 复合类型
     * @return {@link CompositeRule }
     */
    public static CompositeRule defaults(CompositeTypeEnum compositeType) {
        if (CompositeTypeEnum.ACTIVATION.equals(compositeType)) {
            return new ActivationRuleGroup();
        } else if (CompositeTypeEnum.CONDITIONAL.equals(compositeType)) {
            return new ConditionalRuleGroup();
        } else {
            return new UnitRuleGroup();
        }

    }

    /**
     * 获取复合规则实例
     *
     * @param compositeType 复合类型
     * @param name          规则名称
     * @return {@link CompositeRule }
     */
    public static CompositeRule defaults(CompositeTypeEnum compositeType, String name) {
        if (CompositeTypeEnum.ACTIVATION.equals(compositeType)) {
            return new ActivationRuleGroup(name);
        } else if (CompositeTypeEnum.CONDITIONAL.equals(compositeType)) {
            return new ConditionalRuleGroup(name);
        } else {
            return new UnitRuleGroup(name);
        }
    }

    /**
     * 获取复合规则实例
     *
     * @param compositeType 复合类型
     * @param name          规则名称
     * @param description   规则描述
     * @return {@link CompositeRule }
     */
    public static CompositeRule defaults(CompositeTypeEnum compositeType, String name, String description) {
        if (CompositeTypeEnum.ACTIVATION.equals(compositeType)) {
            return new ActivationRuleGroup(name, description);
        } else if (CompositeTypeEnum.CONDITIONAL.equals(compositeType)) {
            return new ConditionalRuleGroup(name, description);
        } else {
            return new UnitRuleGroup(name, description);
        }

    }
}
