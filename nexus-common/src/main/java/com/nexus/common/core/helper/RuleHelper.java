package com.nexus.common.core.helper;

import com.nexus.common.core.rule.*;
import com.nexus.common.enums.CompositeTypeEnum;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.spel.SpELRule;
import org.jeasy.rules.support.composite.CompositeRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * 规则引擎助手
 *
 * @author wk
 * @date 2025/07/29
 */
public class RuleHelper {

    private RuleHelper() {
    }

    /**
     * 创建 SpEL 规则
     *
     * @param ruleModel 规则模型
     * @return {@link Rule }
     */
    public static Rule createSpELRule(RuleModel ruleModel) {
        SpELRule spELRule = SpELRules.defaults()
                .name(ruleModel.getName())
                .description(ruleModel.getDescription())
                .priority(ruleModel.getPriority())
                .when(ruleModel.getCondition())
                .then(ruleModel.getActions().getFirst());
        for (String action : ruleModel.getActions()) {
            spELRule.then(action);
        }
        return spELRule;
    }

    /**
     * 创建 MVEL 规则
     *
     * @param ruleModel 规则模型
     * @return {@link Rule }
     */
    public static Rule createMVELRule(RuleModel ruleModel) {
        MVELRule mvelRule = MVELRules.defaults()
                .name(ruleModel.getName())
                .description(ruleModel.getDescription())
                .priority(ruleModel.getPriority())
                .when(ruleModel.getCondition());
        for (String action : ruleModel.getActions()) {
            mvelRule.then(action);
        }
        return mvelRule;
    }

    /**
     * 创建复合规则
     *
     * @param compositeType 复合类型
     * @return {@link CompositeRule }
     */
    public static CompositeRule createCompositeRule(CompositeTypeEnum compositeType) {
        return CompositeRules.defaults(compositeType);
    }

    /**
     * 创建复合规则
     *
     * @param compositeType 复合类型
     * @param name          规则名称
     * @return {@link CompositeRule }
     */
    public static CompositeRule createCompositeRule(CompositeTypeEnum compositeType, String name) {
        return CompositeRules.defaults(compositeType, name);
    }

    /**
     * 创建复合规则
     *
     * @param compositeType 复合型
     * @param name          规则名称
     * @param description   规则描述
     * @return {@link CompositeRule }
     */
    public static CompositeRule createCompositeRule(CompositeTypeEnum compositeType, String name, String description) {
        return CompositeRules.defaults(compositeType, name, description);
    }

    /**
     * 规则模型
     *
     * @return {@link RuleModel }
     */
    public static RuleModel ruleModel() {
        return new RuleModel();
    }

    /**
     * 规则生成器
     *
     * @return {@link RuleBuilder }
     */
    public static RuleBuilder ruleBuilder() {
        return new RuleBuilder();
    }

    /**
     * 事实对象
     *
     * @return {@link Facts }
     */
    public static Facts facts() {
        Facts facts = new Facts();
        facts.put("LocalDateTime", LocalDateTime.class);
        facts.put("LocalDate", LocalDate.class);
        facts.put("DateTimeFormatter", DateTimeFormatter.class);
        facts.put("Math", Math.class);
        return facts;
    }

    /**
     * 规则集
     *
     * @return {@link Rules }
     */
    public static Rules rules() {
        return new Rules();
    }

    /**
     * 规则集
     *
     * @param ruleModels 规则模型
     * @return {@link Rules }
     */
    public static Rules rules(Collection<RuleModel> ruleModels) {
        Rules rules = rules();
        for (RuleModel ruleModel : ruleModels) {
            if (ruleModel.isEnabled()) {
                rules.register(createMVELRule(ruleModel));
            }
        }
        return rules;
    }

    /**
     * 复合规则集
     *
     * @param ruleModels    规则模型
     * @param compositeType 复合类型
     * @return {@link Rules }
     */
    public static Rules rules(Collection<RuleModel> ruleModels, CompositeTypeEnum compositeType) {
        Rules rules = rules();
        CompositeRule compositeRule = createCompositeRule(compositeType);
        for (RuleModel ruleModel : ruleModels) {
            if (ruleModel.isEnabled()) {
                compositeRule.addRule(createMVELRule(ruleModel));
            }
        }
        rules.register(compositeRule);
        return rules;
    }

    /**
     * 复合规则集
     *
     * @param ruleModels    规则模型
     * @param compositeType 复合类型
     * @return {@link Rules }
     */
    public static Rules rules(Collection<RuleModel> ruleModels, CompositeTypeEnum compositeType, String name) {
        Rules rules = rules();
        CompositeRule compositeRule = createCompositeRule(compositeType, name);
        for (RuleModel ruleModel : ruleModels) {
            if (ruleModel.isEnabled()) {
                compositeRule.addRule(createMVELRule(ruleModel));
            }
        }
        rules.register(compositeRule);
        return rules;
    }

    /**
     * 复合规则集
     *
     * @param ruleModels    规则模型
     * @param compositeType 复合类型
     * @return {@link Rules }
     */
    public static Rules rules(Collection<RuleModel> ruleModels, CompositeTypeEnum compositeType, String name, String description) {
        Rules rules = rules();
        CompositeRule compositeRule = createCompositeRule(compositeType, name, description);
        for (RuleModel ruleModel : ruleModels) {
            if (ruleModel.isEnabled()) {
                compositeRule.addRule(createMVELRule(ruleModel));
            }
        }
        rules.register(compositeRule);
        return rules;
    }

    /**
     * 规则执行器
     *
     * @return {@link RuleExecutor }
     */
    public static RuleExecutor ruleExecutor() {
        return RuleExecutor.newInstance();
    }
}
