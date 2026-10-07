package com.nexus.common.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ip搜索器属性
 *
 * @author wk
 * @date 2026/10/6 22:21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus.ip-searcher")
public class IpSearcherProperties {

    /**
     * ip映射文件存放路径
     */
    private String dbPath = "ip2region.xdb";
}
