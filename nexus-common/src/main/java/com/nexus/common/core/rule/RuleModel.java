package com.nexus.common.core.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeasy.rules.support.RuleDefinition;

/**
 * 规则模型
 *
 * @author wk
 * @date 2025/09/15
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RuleModel extends RuleDefinition {

    /**
     * 启用
     */
    private boolean enabled = true;
}
