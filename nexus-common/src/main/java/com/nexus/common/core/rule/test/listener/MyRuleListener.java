package com.nexus.common.core.rule.test.listener;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;

@Slf4j
public class MyRuleListener implements RuleListener {

    @Override
    public boolean beforeEvaluate(Rule rule, Facts facts) {
        log.info("====> 规则条件判断前: {}, {}", rule.getName(), facts);
        return true;
    }

    @Override
    public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
        log.info("====> 规则条件判断后: {}, {}", rule.getName(), facts);
    }

    @Override
    public void onEvaluationError(Rule rule, Facts facts, Exception exception) {
        log.info("====> 规则条件判断错误: {}, {}, {}", rule.getName(), facts, exception.getMessage());

    }

    @Override
    public void beforeExecute(Rule rule, Facts facts) {
        log.info("====> 规则执行前: {}, {}", rule.getName(), facts);
    }

    @Override
    public void onSuccess(Rule rule, Facts facts) {
        log.info("====> 规则执行成功: {}, {}", rule.getName(), facts);
    }

    @Override
    public void onFailure(Rule rule, Facts facts, Exception exception) {
        log.info("====> 规则执行失败: {}, {}", rule.getName(), facts);
    }
}
