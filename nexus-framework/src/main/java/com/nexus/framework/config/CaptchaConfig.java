package com.nexus.framework.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import com.nexus.common.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 验证码 配置
 *
 * @author wk
 * @date 2024/12/21
 */
@Component
@Slf4j
public class CaptchaConfig {

    /**
     * 资源存储
     */
    @jakarta.annotation.Resource
    private ResourceStore resourceStore;

    /**
     * 初始化图形验证码默认资源
     */
    @PostConstruct
    @ConditionalOnProperty(prefix = "captcha",name = "init-default-resource",havingValue = "true")
    public void initDefaultResource() {
        getBackImageNameList().forEach(imageName -> {
            resourceStore.addResource(CaptchaTypeConstant.SLIDER,
                    new Resource("classpath",String.format("static/images/%s", imageName), CommonConstant.DEFAULT_TAG));
        });
        log.info("====> 图形验证码默认资源初始化完成");
    }

    /**
     * 获取验证码背景图片列表
     *
     * @return int
     */
    private List<String> getBackImageNameList() {
        try {
            return FileUtils.getResourceFileNames("static/images", "jpg");
        } catch (IOException e) {
            log.error("验证码背景图片加载失败, {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
