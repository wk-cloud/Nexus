package com.nexus.common.core.config.properties;

import com.nexus.common.core.enums.ThreadPoolPolicyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池属性
 *
 * @author wk
 * @date 2026/10/6 21:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus.thread.pool")
public class ThreadPoolProperties {


    /**
     * cpu核心数量
     */
    private int cpuCount = Runtime.getRuntime().availableProcessors();

    /**
     * 核心线程数量大小
     */
    private int corePoolSize = Math.clamp(cpuCount - 1, 2, 4);

    /**
     * 线程池最大容纳线程数
     */
    private int maxPoolSize = cpuCount * 2 + 1;

    /**
     * 工作队列数量
     */
    private int workQueue = 20;

    /**
     * 线程空闲后的存活时长
     */
    private int keepAliveTime = 30;

    /**
     * 拒绝策略
     */
    private ThreadPoolPolicyEnum policy = ThreadPoolPolicyEnum.CALLER_RUNS;
}
