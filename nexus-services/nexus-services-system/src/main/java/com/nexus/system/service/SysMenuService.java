package com.nexus.system.service;

import com.nexus.common.mybatisplus.core.mapper.IServicePlus;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.dto.SysMenuDto;
import com.nexus.system.domain.vo.SysMenuVo;

import java.util.List;

/**
 * 权限服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysMenuService extends IServicePlus<SysMenu, SysMenuVo> {

    /**
     * 根据id，获取权限
     * @param permissionId 权限id
     * @return {@link SysMenuVo}
     */
    SysMenuVo getPermissionById(Long permissionId);

    /**
     * 删除权限
     *
     * @param permissionId 权限id
     * @return {@link Boolean}
     */
    Boolean deletePermission(Long permissionId);

    /**
     * 更新权限
     *
     * @param sysMenuDto 系统权限
     * @return {@link Boolean}
     */
    Boolean updatePermission(SysMenuDto sysMenuDto);

    /**
     * 添加权限
     *
     * @param sysMenuDto 系统权限
     * @return {@link Boolean}
     */
    Boolean addPermission(SysMenuDto sysMenuDto);

    /**
     * 查询权限列表
     *
     * @param sysMenuDto 系统权限
     * @return {@link List}<{@link SysMenuVo}>
     */
    List<SysMenuVo> listPermission(SysMenuDto sysMenuDto);
}
