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
@ConfigurationProperties(prefix = "nexus.upload")
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
     * @description
     * 这里指 nginx 中的文件访问代理路径。
     * 例如：指定代理路径是 /files，那么访问文件时的路径就是 http://localhost:8080/files/xxx.png
     */
    private String proxyPath = "/files";

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
     * 文件服务器地址
     * @description
     * 这里指文件服务器的地址，用于拼接文件的完整访问地址，完整访问地址是文件服务器地址 + 代理路径 + 文件名。
     * 例如：指定文件服务器地址是 http://localhost:8080，代理路径是 /files，那么文件的完整访问地址就是 http://localhost:8080/files/xxx.png
     */
    private String baseUrl;

    /**
     * 文件保存的基础目录
     * @description
     * 这里指文件保存的基础目录，但不是最终目录，最终目录是基础目录 + 代理路径共同组成。
     * 例如：指定基础目录是 /data/upload，代理路径是 /files，那么文件最终将保存在 /data/upload/files/xxx.png
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
