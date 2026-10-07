package com.nexus.common.core.config;

import com.nexus.common.core.config.properties.ThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * 线程池配置
 *
 * @author wk
 * @date 2025/08/03
 */
@Slf4j
@EnableScheduling
@Configuration
public class ThreadPoolConfig {

    /**
     * 线程池属性
     */
    private final ThreadPoolProperties threadPoolProperties;

    public ThreadPoolConfig(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }

    @ConditionalOnMissingBean
    @Bean("asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        //等待队列
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getWorkQueue());
        //线程前缀
        threadPoolTaskExecutor.setThreadNamePrefix("asyncTaskExecutor-");
        //线程池维护线程所允许的空闲时间,单位为秒
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveTime());
        // 线程池对拒绝任务(无线程可用)的处理策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(threadPoolProperties.getPolicy().getHandler());
        threadPoolTaskExecutor.initialize();

        log.info("====> 线程池初始化完成");
        return threadPoolTaskExecutor;
    }

    @ConditionalOnMissingBean
    @Bean("asyncTaskScheduler")
    public TaskScheduler getAsyncScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(threadPoolProperties.getSchedulerPoolSize());
        log.info("====> 定时任务配置完成");
        return threadPoolTaskScheduler;
    }
}
