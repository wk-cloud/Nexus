package com.nexus.framework.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger配置
 *
 * @author wk
 * @date 2022/7/20
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customerOpenApi() {
        return new OpenAPI().info(new Info()
                        .title("nexus接口文档")
                        .description("基于 SpringBoot3 + Vue3 的前后端分离的web开发模版")
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .url("https://wk-blog.vip")
                        .description("我的个人博客"));
    }
}
