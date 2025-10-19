package com.nexus.framework.config;


import com.nexus.framework.shiro.CustomerHashedCredentialsMatcher;
import com.nexus.framework.shiro.CustomerRealm;
import com.nexus.framework.shiro.filter.CustomerAccessControlFilter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


/**
 * shiro配置
 *
 * @author wk
 * @date 2025/09/17
 */
@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = {ShiroWebFilterConfiguration.class})
public class ShiroConfig {

    @Resource
    private CustomerHashedCredentialsMatcher customerHashedCredentialsMatcher;
    @Resource
    private CustomerRealm customerRealm;

    /**
     * 请求路径的处理方式map
     */
    private final LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

    /**
     * 允许放行的请求路径
     */
    private final List<String> isAccessAllowedUrlList = new ArrayList<>();

    /**
     * 自定义 realm，相当于一个数据源
     *
     * @return {@link CustomerRealm}
     */
    //@Bean
    @PostConstruct
    public CustomerRealm getRealm() {
        // 设置自定义认证方式
        customerRealm.setCredentialsMatcher(customerHashedCredentialsMatcher);
        // 开启缓存
        //customerRealm.setCacheManager(new EhCacheManager()); // shiro 自带的缓存
        //customerRealm.setCacheManager(new RedisCacheManager()); // redis 缓存
        //// 开启全局缓存
        //customerRealm.setCachingEnabled(true);
        //// 开启认证缓存
        //customerRealm.setAuthenticationCachingEnabled(true);
        //// 认证缓存命名
        //customerRealm.setAuthenticationCacheName("authenticationCache");
        //// 开启授权缓存
        //customerRealm.setAuthorizationCachingEnabled(true);
        //// 授权缓存命名
        //customerRealm.setAuthorizationCacheName("authorizationCache");
        return customerRealm;
    }


    /**
     * 默认web安全管理器
     *
     * @param customerRealm 自定义Realm
     * @return {@link DefaultWebSecurityManager}
     */
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(CustomerRealm customerRealm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(customerRealm);
        // 关闭shiro自带的session
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        defaultSubjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        defaultWebSecurityManager.setSubjectDAO(defaultSubjectDAO);
        return defaultWebSecurityManager;
    }


    /**
     * shiroFilter ，拦截所有请求
     *
     * @param defaultWebSecurityManager 默认web安全管理器
     * @return {@link ShiroFilterFactoryBean}
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 添加自定义的过滤器
        LinkedHashMap<String, Filter> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("customerAuthFilter", new CustomerAccessControlFilter());
        shiroFilterFactoryBean.setFilters(linkedHashMap);
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        // 静态资源地址
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/files/**", "anon");

        // 接口文档地址
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v3/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");

        // websocket
        filterChainDefinitionMap.put("/ws/**", "anon");

        // 所有请求都要经过自定义的过滤器进行登录认证
        filterChainDefinitionMap.put("/**", "customerAuthFilter");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 获取过滤器链定义映射
     *
     * @return {@link LinkedHashMap}<{@link String},{@link String}>
     */
    @Bean
    public LinkedHashMap<String, String> getFilterChainDefinitionMap() {
        return this.filterChainDefinitionMap;
    }

    /**
     * 获取允许放行的url列表
     *
     * @return {@link List}<{@link String}>
     */
    @Bean
    public List<String> getIsAccessAllowedUrlList() {
        LinkedHashMap<String, String> filterChainDefinitionMap = this.getFilterChainDefinitionMap();
        Set<String> keySet = filterChainDefinitionMap.keySet();
        keySet.stream().filter(key -> "anon".equalsIgnoreCase(filterChainDefinitionMap.get(key))).forEach(isAccessAllowedUrlList::add);
        return isAccessAllowedUrlList;
    }

    /**
     * 生命周期bean后置处理程序
     * 管理Shiro对象生命周期
     *
     * @return {@link LifecycleBeanPostProcessor}
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }


    /**
     * 开启shiro注解模式
     *
     * @param securityManager 安全管理器
     * @return {@link AuthorizationAttributeSourceAdvisor}
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;

    }
}
