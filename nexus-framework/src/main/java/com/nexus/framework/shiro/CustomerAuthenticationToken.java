package com.nexus.framework.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 客户身份验证令牌
 * 取代 shiro 的token，使用自定义的 JWT token
 * @author wk
 * @date 2022/7/19 20:27
 */
public class CustomerAuthenticationToken implements AuthenticationToken {

    private final String token;

    public CustomerAuthenticationToken(String token){
        this.token = token;
    }


    /**
     * 获取主体对象用户信息token（之前是用户名）
     *
     * @return {@link Object }
     */
    @Override
    public Object getPrincipal() {
        return token;
    }


    /**
     * 获取整体对象的凭证信息token（之前是密码）
     * @return {@link Object }
     */
    @Override
    public Object getCredentials() {
        return token;
    }
}
