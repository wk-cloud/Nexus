package com.nexus.common.oss.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置
 *
 * @author wk
 * @date 2026/6/19 15:27
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload")
public class FileUploadProperties {

    /**
     * 静态资源路径
     */
    private String resourcePath = "/public";

    /**
     * 临时资源路径
     */
    private String tempResourcePath = "/temp";

    /**
     * 默认代理路径
     */
    private String defaultProxyPath = "/files";

    /**
     * 端口号
     */
    @Value("${server.port}")
    private Integer port;

    /**
     * 是否为生产环境
     */
    private Boolean prod = false;

    /**
     * 服务器网址
     */
    private String baseUrl;

    /**
     * 文件保存的基目录
     */
    private String basePath;

    /**
     * 是否是生产环境
     *
     * @return boolean
     */
    public boolean isProd() {
        return prod;
    }
}
