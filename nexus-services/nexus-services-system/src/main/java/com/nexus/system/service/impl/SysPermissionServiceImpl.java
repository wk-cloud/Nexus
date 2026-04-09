package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.core.enums.PermissionStateEnum;
import com.nexus.common.core.service.PermissionService;
import com.nexus.common.core.utils.CollectionUtils;
import com.nexus.common.core.utils.StringUtils;
import com.nexus.common.shiro.helper.LoginHelper;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.SysRoleMenu;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysPermissionServiceImpl
 *
 * @author wk
 * @date 2026/4/7 16:51
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class SysPermissionServiceImpl implements SysPermissionService, PermissionService {

    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysRoleMenuService roleMenuService;
    @Resource
    private SysUserRoleService userRoleService;

    /**
     * 获取角色权限
     * @param userId 用户id
     * @return {@link Set }<{@link String }>
     */
    @Override
    public Set<String> getRolePermission(Long userId) {
        Set<Long> roleIds = getRoleIds(userId);
        if(CollectionUtils.isEmpty(roleIds)) {
            return new HashSet<>();
        }
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.select(SysRole::getLabel)
                .isNotNull(SysRole::getLabel)
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getState, PermissionStateEnum.NORMAL.getCode());
        return sysRoleService.list(roleLambdaQueryWrapper).stream().map(SysRole::getLabel).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    /**
     * 获取菜单权限
     *
     * @param userId 用户id
     * @return {@link Set }<{@link String }>
     */
    @Override
    public Set<String> getMenuPermission(Long userId) {
        if(LoginHelper.isSuperAdmin()) {
            LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            menuLambdaQueryWrapper.select(SysMenu::getPerms)
                    .isNotNull(SysMenu::getPerms)
                    .eq(SysMenu::getState, PermissionStateEnum.NORMAL.getCode());;
            return sysMenuService.list(menuLambdaQueryWrapper).stream().map(SysMenu::getPerms).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        }
        Set<Long> roleIds = getRoleIds(userId);
        if(CollectionUtils.isEmpty(roleIds)) {
            return new HashSet<>();
        }
        LambdaQueryWrapper<SysRoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleMenuLambdaQueryWrapper.select(SysRoleMenu::getMenuId).in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> roleMenuList = roleMenuService.list(roleMenuLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(roleMenuList)) {
            return new HashSet<>();
        }
        Set<Long> menuIds = roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
        LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        menuLambdaQueryWrapper.select(SysMenu::getPerms)
                .isNotNull(SysMenu::getPerms)
                .in(SysMenu::getId, menuIds)
                .eq(SysMenu::getState, PermissionStateEnum.NORMAL.getCode());
        return sysMenuService.list(menuLambdaQueryWrapper).stream().map(SysMenu::getPerms).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    /**
     * 获取角色id集合
     *
     * @param userId 用户id
     * @return {@link Set }<{@link Long }>
     */
    private Set<Long> getRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.select(SysUserRole::getRoleId).eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoleList = userRoleService.list(userRoleLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(userRoleList)) {
            return new HashSet<>();
        }
        return userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
    }
}
