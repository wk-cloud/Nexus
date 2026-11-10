package com.nexus.common.verificationcode.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import com.nexus.common.core.utils.FileUtils;
import com.nexus.common.verificationcode.config.properties.CaptchaProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 验证码 配置
 *
 * @author wk
 * @date 2024/12/21
 */
@RequiredArgsConstructor
@Configuration
@Slf4j
public class CaptchaConfig {

    /**
     * 资源存储
     */
    private final ResourceStore resourceStore;

    /**
     * 验证码配置属性
     */
    private final CaptchaProperties captchaProperties;

    /**
     * 初始化图形验证码默认资源
     */
    @PostConstruct
    @ConditionalOnProperty(prefix = "captcha", name = "init-default-resource", havingValue = "true")
    public void initDefaultResource() {
        String filePath = FileUtils.removeLastFileSeparator(captchaProperties.getFilePath());
        getBackgroundImageNameList(filePath).forEach(imageName -> {
            resourceStore.addResource(CaptchaTypeConstant.SLIDER,
                    new Resource("classpath", String.format("%s/%s", filePath, imageName), CommonConstant.DEFAULT_TAG));
        });
        log.info("====> 图形验证码默认资源初始化完成");
    }

    /**
     * 获取验证码背景图片列表
     *
     * @param filePath 文件路径
     * @return int
     */
    private List<String> getBackgroundImageNameList(String filePath) {
        try {
            return FileUtils.getResourceFileNames(filePath, captchaProperties.getSuffix());
        } catch (IOException e) {
            log.error("====> 验证码背景图片加载失败", e);
        }
        return Collections.emptyList();
    }
}
