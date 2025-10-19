package com.nexus.framework.shiro;

import com.nexus.common.utils.TokenUtils;
import com.nexus.system.service.SysOnlineUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.stereotype.Component;


/**
 * 自定义凭证匹配器
 *
 * @author wk
 * @date 2025/09/17
 */
@Component
@Slf4j
public class CustomerHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Resource
    private SysOnlineUserService sysOnlineUserService;

    /**
     * 凭据是否匹配
     *
     * @param token 令 牌
     * @param info  信息
     * @return boolean
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        CustomerAuthenticationToken customerAuthenticationToken = (CustomerAuthenticationToken) token;
        String accessToken = (String) customerAuthenticationToken.getCredentials();
        // 令牌过期拒绝访问
        if (TokenUtils.isExpired(accessToken)) {
            // 删除过期token
            TokenUtils.removeTokenFromRedisSet(accessToken);
            // 删除在线用户
            sysOnlineUserService.removeOfflineUser(accessToken);
            return false;
        }
        // 令牌未过期，但不存在于系统拒绝访问
        return TokenUtils.isExistOfRedisSet(accessToken);
    }
}
