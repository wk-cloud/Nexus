package com.nexus.common.core.rule.test;

import com.nexus.common.core.rule.RuleExecutor;
import com.nexus.common.core.helper.RuleHelper;
import com.nexus.common.core.rule.RuleModel;
import com.nexus.common.core.rule.test.action.Console;
import com.nexus.common.core.rule.test.listener.MyRuleListener;
import com.nexus.common.core.rule.test.listener.MyRulesEngineListener;
import com.nexus.common.core.rule.test.variable.Order;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试规则
 *
 * @author wk
 * @date 2025/07/29
 */
@Slf4j
public class RuleTest {
    public static void main(String[] args) {

        // 生成规则定义列表
        List<RuleModel> ruleModelList = createRuleModels();

        // 规则集
        Rules rules = RuleHelper.rules(ruleModelList);

        // 事实对象
        Facts facts = RuleHelper.facts();
        facts.put("console", Console.class);
        facts.put("order", new Order("1", 3, "测试订单"));
        facts.put("coupon", 3);

        // 规则执行器
        RuleExecutor ruleExecutor = RuleHelper.ruleExecutor()
                .rules(rules)
                .facts(facts)
                .registerRuleListener(new MyRuleListener())
                .registerRulesEngineListener(new MyRulesEngineListener());

        // 运行规则
        ruleExecutor.run();

        // 规则执行后的事实对象
        log.info("====> facts: {}", facts);
    }

    /**
     * 创建规则模型
     *
     * @return {@link List }<{@link RuleModel }>
     */
    public static List<RuleModel> createRuleModels() {
        List<RuleModel> ruleModelList = new ArrayList<>();

        RuleModel ruleModel = new RuleModel();
        ruleModel.setName("test1");
        ruleModel.setDescription("测试规则1");
        ruleModel.setPriority(1);
        ruleModel.setCondition("true");
        ruleModel.getActions().add("System.out.println(\"输出数字：\" + 123)");
        ruleModel.getActions().add("console.print()");
        ruleModelList.add(ruleModel);

        ruleModel = new RuleModel();
        ruleModel.setName("test2");
        ruleModel.setDescription("测试规则2");
        ruleModel.setPriority(2);
        ruleModel.setCondition("true");
        ruleModel.getActions().add("System.out.println(\"输出数字：\" + 456)");
        ruleModel.getActions().add("console.print()");
        ruleModelList.add(ruleModel);

        ruleModel = new RuleModel();
        ruleModel.setName("test3");
        ruleModel.setDescription("测试规则3");
        ruleModel.setPriority(3);
        ruleModel.setCondition("order.getPrice() > 2");
        ruleModel.getActions().add("System.out.println(\"当前商品价格：\" + order.getPrice() + \"元\n商品价格超过2元使用，\" + coupon + \"元优惠券\")");
        ruleModel.getActions().add("order.setPrice(order.getPrice() - coupon)");
        ruleModel.getActions().add("System.out.println(\"商品使用优惠券后的价格：\" + order.getPrice() + \"元\")");
        ruleModelList.add(ruleModel);

        ruleModel = new RuleModel();
        ruleModel.setEnabled(true);
        ruleModel.setName("test4");
        ruleModel.setDescription("测试规则4");
        ruleModel.setPriority(4);
        ruleModel.setCondition("order.getPrice() == 0");
        ruleModel.getActions().add("System.out.println(\"当前商品价格：\" + order.getPrice() + \"元，已达到最大优惠力度\")");
        ruleModelList.add(ruleModel);


        return ruleModelList;
    }
}
