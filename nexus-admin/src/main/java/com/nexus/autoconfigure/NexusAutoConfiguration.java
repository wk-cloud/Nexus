package com.nexus.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Nexus 自动配置类
 * 作为项目底座，支持跨包扫描 com.nexus 下的所有类
 * @author wk
 * @date 2026/9/21 0:47
 */
@AutoConfiguration
@ComponentScan(
        basePackages = "com.nexus",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.nexus\\.autoconfigure\\..*"
        )
)
public class NexusAutoConfiguration {
}
