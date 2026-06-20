package com.nexus.common.token.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 令牌配置
 *
 * @author wk
 * @date 2026/6/19 15:18
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    /**
     * 签名
     */
    private String signature;

    /**
     * 过期时间
     */
    private String expiration;
}
