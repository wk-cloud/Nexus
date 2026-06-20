package com.nexus.auth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * qq登录配置
 *
 * @author wk
 * @date 2026/6/19 16:31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qq.login")
public class QqLoginProperties {

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 重定向地址
     */
    private String redirectUri;

    /**
     * 格式
     */
    private String fmt;
}
