package com.nexus.framework.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * MyBatisPlus 配置类
 *
 * @author wk
 * @date 2022/7/16
 */
@MapperScan({"com.nexus.*.mapper", "com.*.*.mapper"})
@EnableTransactionManagement
@Configuration
public class MyBatisPlusConfig {

    /**
     * 自定义id生成策略(生成16位雪花id)
     * false：禁用
     * true：启用
     *
     * @return {@link IdentifierGenerator}
     */
    @Bean
    @ConditionalOnExpression("true")
    public IdentifierGenerator identifierGenerator() {
        return new CustomerIdGenerator();
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // MyBatisPlus 拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setMaxLimit(500L);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 添加乐观锁
        OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(optimisticLockerInnerInterceptor);
        return mybatisPlusInterceptor;
    }
}
