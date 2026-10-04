package com.nexus.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CorsProperties
 *
 * @author wk
 * @date 2026/10/4 19:33
 */
@Data
@ConfigurationProperties(prefix = "nexus.cors")
public class CorsProperties {
    /** 是否开启全局跨域 */
    private boolean enabled = true;
    /** 允许的来源，支持通配 */
    private List<String> allowedOriginPatterns = List.of("*");
    /** 允许的方法 */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    /** 允许的请求头 */
    private List<String> allowedHeaders = List.of("*");
    /** 是否允许携带凭证 */
    private boolean allowCredentials = true;
    /** 预检请求缓存时间(秒) */
    private long maxAge = 3600;
    /** 生效路径 */
    private String mapping = "/**";
}
