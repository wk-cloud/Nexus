package com.nexus.framework.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.enums.AdminEnum;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.common.utils.TokenUtils;
import com.nexus.system.domain.SysPermission;
import com.nexus.system.domain.SysRolePermission;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.service.SysPermissionService;
import com.nexus.system.service.SysRolePermissionService;
import com.nexus.system.service.SysUserRoleService;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRolePermissionService sysRolePermissionService;
    @Resource
    private SysPermissionService sysPermissionService;


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
                Long userId = Long.parseLong((String) TokenUtils.getValueFromToken(token, "userId"));
                // 1. 查询角色列表
                List<SysRoleVo> roleList = sysUserRoleService.queryRoleListByUserId(userId);
                if (CollectionUtils.isEmpty(roleList)) {
                    return null;
                }
                // 角色标签列表
                Set<String> roleSet = roleList.stream().map(SysRoleVo::getLabel).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                simpleAuthorizationInfo.setRoles(roleSet);

                // 权限标签列表
                Set<String> permissionSet;
                if (roleSet.contains(AdminEnum.SUPER_ADMIN.getLabel())) {
                    // 超级管理员拥有所有权限
                    permissionSet = sysPermissionService.list().stream().map(SysPermission::getPerms).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                } else {
                    List<Long> roleIdList = roleList.stream().map(SysRoleVo::getId).collect(Collectors.toList());
                    // 2. 查询权限列表
                    LambdaQueryWrapper<SysRolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    rolePermissionLambdaQueryWrapper.in(SysRolePermission::getRoleId, roleIdList);
                    List<SysRolePermission> rolePermissionList = sysRolePermissionService.list(rolePermissionLambdaQueryWrapper);
                    if (CollectionUtils.isEmpty(rolePermissionList)) {
                        return null;
                    }
                    List<Long> permissionIdList = rolePermissionList.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
                    List<SysPermission> permissionList = sysPermissionService.listByIds(permissionIdList);
                    permissionSet = permissionList.stream().map(SysPermission::getPerms)
                            .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                }
                simpleAuthorizationInfo.setStringPermissions(permissionSet);
                return simpleAuthorizationInfo;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
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
