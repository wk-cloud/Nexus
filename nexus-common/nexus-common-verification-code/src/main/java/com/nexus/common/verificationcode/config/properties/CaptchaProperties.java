package com.nexus.common.verificationcode.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码属性
 *
 * @author wk
 * @date 2026/11/9 21:40
 */
@Data
@Component
@ConfigurationProperties("nexus.captcha.background")
public class CaptchaProperties {

    /**
     * 验证码背景图片文件路径
     * 默认值：static/images/verification
     * 路径格式：不带文件名和后缀
     */
    private String filePath = "static/images/verification";

    /**
     * 验证码背景图片后缀
     * 默认值：.jpg
     */
    private String suffix = ".jpg";
}
