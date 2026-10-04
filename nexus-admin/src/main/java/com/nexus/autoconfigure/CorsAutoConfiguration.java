package com.nexus.autoconfigure;

import com.nexus.autoconfigure.properties.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Cors 自动配置类
 *
 * @author wk
 * @date 2026/10/4 19:35
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@ConditionalOnProperty(prefix = "nexus.cors", name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class CorsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)   // 允许业务方覆盖
    public CorsFilter corsFilter(CorsProperties props) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(props.getAllowedOriginPatterns());
        config.setAllowedMethods(props.getAllowedMethods());
        config.setAllowedHeaders(props.getAllowedHeaders());
        config.setAllowCredentials(props.isAllowCredentials());
        config.setMaxAge(props.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(props.getMapping(), config);

        log.info("====> CorsFilter 初始化完成");
        return new CorsFilter(source);
    }
}
