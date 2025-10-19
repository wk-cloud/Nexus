package com.nexus.common.core.rule;

import org.jeasy.rules.api.*;
import org.jeasy.rules.core.DefaultRulesEngine;

import java.util.List;

/**
 * 规则执行器
 *
 * @author wk
 * @date 2025/09/15
 */
public class RuleExecutor {

    /**
     * 当一个规则成功应用时，跳过余下的规则
     */
    private boolean skipOnFirstAppliedRule;

    /**
     * 当一个规则失败时，跳过余下的规则
     */
    private boolean skipOnFirstFailedRule;

    /**
     * 当一个规则未触发时，跳过余下的规则
     */
    private boolean skipOnFirstNonTriggeredRule;

    /**
     * 当优先级超过指定的阈值时，跳过余下的规则
     */
    private Integer priorityThreshold = Integer.MAX_VALUE;

    /**
     * 规则
     */
    private Rules rules = new Rules();

    /**
     * 事实对象
     */
    private Facts facts = new Facts();


    /**
     * 规则引擎参数
     */
    private final RulesEngineParameters rulesEngineParameters = new RulesEngineParameters()
            .skipOnFirstAppliedRule(skipOnFirstAppliedRule)
            .skipOnFirstFailedRule(skipOnFirstFailedRule)
            .skipOnFirstNonTriggeredRule(skipOnFirstNonTriggeredRule)
            .priorityThreshold(priorityThreshold);

    /**
     * 规则引擎
     */
    private DefaultRulesEngine rulesEngine = new DefaultRulesEngine(rulesEngineParameters);

    /**
     * 私有化构造器
     */
    private RuleExecutor() {
    }

    /**
     * 新实例
     *
     * @return {@link RuleExecutor }
     */
    public static RuleExecutor newInstance() {
        return new RuleExecutor();
    }

    public boolean skipOnFirstAppliedRule() {
        return skipOnFirstAppliedRule;
    }

    public RuleExecutor skipOnFirstAppliedRule(boolean skipOnFirstAppliedRule) {
        this.skipOnFirstAppliedRule = skipOnFirstAppliedRule;
        this.rulesEngineParameters.skipOnFirstAppliedRule(skipOnFirstAppliedRule);
        this.rulesEngine.getParameters().skipOnFirstAppliedRule(skipOnFirstAppliedRule);
        return this;
    }

    public boolean skipOnFirstFailedRule() {
        return skipOnFirstFailedRule;
    }

    public RuleExecutor skipOnFirstFailedRule(boolean skipOnFirstFailedRule) {
        this.skipOnFirstFailedRule = skipOnFirstFailedRule;
        this.rulesEngineParameters.skipOnFirstFailedRule(skipOnFirstFailedRule);
        this.rulesEngine.getParameters().skipOnFirstFailedRule(skipOnFirstFailedRule);
        return this;
    }

    public boolean skipOnFirstNonTriggeredRule() {
        return skipOnFirstNonTriggeredRule;
    }

    public RuleExecutor skipOnFirstNonTriggeredRule(boolean skipOnFirstNonTriggeredRule) {
        this.skipOnFirstNonTriggeredRule = skipOnFirstNonTriggeredRule;
        this.rulesEngineParameters.skipOnFirstNonTriggeredRule(skipOnFirstNonTriggeredRule);
        this.rulesEngine.getParameters().skipOnFirstNonTriggeredRule(skipOnFirstNonTriggeredRule);
        return this;
    }

    public Integer priorityThreshold() {
        return priorityThreshold;
    }

    public RuleExecutor priorityThreshold(Integer priorityThreshold) {
        this.priorityThreshold = priorityThreshold;
        this.rulesEngineParameters.priorityThreshold(priorityThreshold);
        this.rulesEngine.getParameters().priorityThreshold(priorityThreshold);
        return this;
    }

    /**
     * 获取规则集
     * @return {@link Rules }
     */
    public Rules rules() {
        return rules;
    }

    /**
     * 设置规则集
     * @param rules 规则集
     * @return {@link RuleExecutor }
     */
    public RuleExecutor rules(Rules rules) {
        this.rules = rules;
        return this;
    }

    /**
     * 获取事实
     *
     * @return {@link Facts }
     */
    public Facts facts() {
        return facts;
    }

    /**
     * 设置事实对象
     *
     * @param facts 事实对象
     * @return {@link RuleExecutor }
     */
    public RuleExecutor facts(Facts facts) {
        this.facts = facts;
        return this;
    }

    /**
     * 获取规则引擎参数
     *
     * @return {@link RulesEngineParameters }
     */
    public RulesEngineParameters rulesEngineParameters(){
        return rulesEngineParameters;
    }

    /**
     * 获取规则引擎
     *
     * @return {@link RuleExecutor }
     */
    public RulesEngine rulesEngine() {
        return rulesEngine;
    }

    /**
     * 注册规则监听器
     *
     * @param ruleListener 规则监听器
     * @return {@link RuleExecutor }
     */
    public RuleExecutor registerRuleListener(RuleListener ruleListener) {
        this.rulesEngine.registerRuleListener(ruleListener);
        return this;
    }

    /**
     * 批量注册规则监听器
     *
     * @param ruleListeners 规则监听器
     * @return {@link RuleExecutor }
     */
    public RuleExecutor registerRuleListeners(List<RuleListener> ruleListeners) {
        this.rulesEngine.registerRuleListeners(ruleListeners);
        return this;
    }

    /**
     * 注册规则引擎监听器
     *
     * @param rulesEngineListener 规则引擎监听器
     * @return {@link RuleExecutor }
     */
    public RuleExecutor registerRulesEngineListener(RulesEngineListener rulesEngineListener) {
        this.rulesEngine.registerRulesEngineListener(rulesEngineListener);
        return this;
    }

    /**
     * 批量注册规则引擎监听器
     *
     * @param rulesEngineListeners 规则引擎监听器
     * @return {@link RuleExecutor }
     */
    public RuleExecutor registerRulesEngineListeners(List<RulesEngineListener> rulesEngineListeners) {
        this.rulesEngine.registerRulesEngineListeners(rulesEngineListeners);
        return this;
    }

    /**
     * 运行规则
     */
    public void run() {
        rulesEngine.fire(rules, facts);
    }

    /**
     * 运行规则
     *
     * @param rules 规则
     * @param facts 事实
     */
    public void run(Rules rules, Facts facts) {
        rulesEngine.fire(rules, facts);
    }
}
