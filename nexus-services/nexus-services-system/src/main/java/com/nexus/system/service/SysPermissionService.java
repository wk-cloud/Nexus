package com.nexus.system.service;


import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.system.domain.SysPermission;
import com.nexus.system.domain.dto.SysPermissionDto;
import com.nexus.system.domain.vo.SysPermissionVo;

import java.util.List;

/**
 * 权限服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysPermissionService extends IServicePlus<SysPermission, SysPermissionVo> {

    /**
     * 根据id，获取权限
     * @param permissionId 权限id
     * @return {@link SysPermissionVo}
     */
    SysPermissionVo getPermissionById(Long permissionId);

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
     * @param sysPermissionDto 系统权限
     * @return {@link Boolean}
     */
    Boolean updatePermission(SysPermissionDto sysPermissionDto);

    /**
     * 添加权限
     *
     * @param sysPermissionDto 系统权限
     * @return {@link Boolean}
     */
    Boolean addPermission(SysPermissionDto sysPermissionDto);

    /**
     * 查询权限列表
     *
     * @param sysPermissionDto 系统权限
     * @return {@link List}<{@link SysPermissionVo}>
     */
    List<SysPermissionVo> listPermission(SysPermissionDto sysPermissionDto);
}
