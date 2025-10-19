package com.nexus.common.core.rule.test.listener;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineListener;

@Slf4j
public class MyRulesEngineListener implements RulesEngineListener {

    @Override
    public void beforeEvaluate(Rules rules, Facts facts) {
        log.info("====> 规则引擎判断前：{}, {}", rules, facts);
    }

    @Override
    public void afterExecute(Rules rules, Facts facts) {
        log.info("====> 规则引擎执行后：{}, {}", rules, facts);
    }
}
