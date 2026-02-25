package com.nexus.common.config;


import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ip搜索器配置
 *
 * @author wk
 * @date 2026/02/23
 */
@Configuration
@Slf4j
public class IpSearcherConfig {
    /**
     * ip映射文件路径
     */
    private final String DB_PATH = "ip2region.xdb";

    @Bean("ipSearcher")
    public Searcher searcher() {
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            ClassPathResource resource = new ClassPathResource(DB_PATH);
            inputStream = resource.getInputStream();
            baos = new ByteArrayOutputStream();
            // 将 ip2region.db 文件读取到字节数组输出流
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 字节输出流转化为字节数组
            byte[] bytes = baos.toByteArray();
            log.info("====> ip搜索器配置完成");
            return Searcher.newWithBuffer(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if(baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
