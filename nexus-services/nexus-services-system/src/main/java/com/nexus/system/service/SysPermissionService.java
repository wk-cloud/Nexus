package com.nexus.system.service;

import java.util.Set;

/**
 * 系统权限服务
 *
 * @author wk
 * @date 2026/04/07
 */
public interface SysPermissionService {

    /**
     * 获取角色权限
     *
     * @param userId 用户id
     * @return {@link Set }<{@link String }>
     */
    Set<String> getRolePermission(Long userId);

    /**
     * 获取菜单权限
     *
     * @param userId 用户id
     * @return {@link Set }<{@link String }>
     */
    Set<String> getMenuPermission(Long userId);
}
