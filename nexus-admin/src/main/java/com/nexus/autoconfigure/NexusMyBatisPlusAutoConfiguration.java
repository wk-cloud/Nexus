package com.nexus.autoconfigure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * NexusMyBatisPlusAutoConfiguration
 *
 * @author wk
 * @date 2026/9/21 18:37
 */
@ConditionalOnMissingBean(name = "nexusMyBatisPlusAutoConfiguration")
@AutoConfiguration
@MapperScan("com.nexus.**.mapper")
public class NexusMyBatisPlusAutoConfiguration {

}
