package com.nexus.common.mail.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件配置
 *
 * @author wk
 * @date 2026/6/19 13:45
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    /**
     * 邮箱账号
     * */
    private String from;

    /**
     * 邮件发送人名称
     * */
    private String fromName;

    /**
     * 文件路径
     */
    private String filePath;
}
