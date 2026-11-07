package com.nexus.common.shiro.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Shiro配置
 *
 * @author wk
 * @date 2026/11/5 23:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus.shiro")
public class ShiroProperties {

    /**
     * 不需要认证的 url 列表
     */
    private List<String> anonUrls = new ArrayList<>();
}
