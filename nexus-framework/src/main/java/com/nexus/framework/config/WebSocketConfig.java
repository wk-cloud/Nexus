package com.nexus.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket 配置
 *
 * @author wk
 * @date 2023/1/24
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig {

    /**
     * 服务器端点出口
     * 用于扫描带有 @ServerEndpoint 的注解成为 websocket
     *
     * @return {@link ServerEndpointExporter}
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        log.info("=====> 初始化 websocket");
        return new ServerEndpointExporter();
    }
}
