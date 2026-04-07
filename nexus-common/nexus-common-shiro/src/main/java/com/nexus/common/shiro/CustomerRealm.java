package com.nexus.common.shiro;

import com.nexus.common.core.service.PermissionService;
import com.nexus.common.core.utils.ObjectUtils;
import com.nexus.common.token.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * 自定义 Realm
 *
 * @author wk
 * @date 2023/07/31
 */
@Component
@Slf4j
public class CustomerRealm extends AuthorizingRealm {
    @Resource
    private PermissionService permissionService;


    /**
     * 重写 supports 方法，让 shiro 支持我们自定的token，即 jwt 生成的 token
     *
     * @param token 令牌
     * @return boolean
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomerAuthenticationToken;
    }


    /**
     * 授权
     *
     * @param principals 与 Subject关联的所有主体
     * @return {@link AuthorizationInfo}
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String token = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        try {
            if (ObjectUtils.isNotNull(TokenUtils.checkToken(token))) {
                log.info("====> 开始进行授权");
                Long userId = Long.parseLong(TokenUtils.getValueFromToken(token, "userId"));
                // 1. 设置角色信息
                Set<String> roleSet = permissionService.getRolePermission(userId);
                simpleAuthorizationInfo.setRoles(roleSet);
                // 2. 权限标签列表
                Set<String> permissionSet = permissionService.getMenuPermission(userId);
                simpleAuthorizationInfo.setStringPermissions(permissionSet);
                return simpleAuthorizationInfo;
            }
        } catch (Exception e) {
            log.error("系统授权异常", e);
        }
        return null;
    }


    /**
     * 认证
     *
     * @param token 令牌
     * @return {@link AuthenticationInfo}
     * @throws AuthenticationException 身份验证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("====> 开始进行认证");
        CustomerAuthenticationToken customerAuthenticationToken = (CustomerAuthenticationToken) token;
        if (ObjectUtils.isNotNull(customerAuthenticationToken)) {
            return new SimpleAuthenticationInfo(customerAuthenticationToken.getPrincipal(),
                    customerAuthenticationToken.getCredentials(), this.getName());
        }
        return null;
    }
}
