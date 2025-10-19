package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.system.domain.SysRolePermission;
import com.nexus.system.domain.vo.SysPermissionVo;

import java.util.Collection;
import java.util.List;

/**
 * 角色权限服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysRolePermissionService extends IServicePlus<SysRolePermission, SysRolePermission> {

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     * @return {@link Boolean}
     */
    Boolean deleteByRoleId(Long roleId);

    /**
     * 通过权限id批量删除
     *
     * @param permissionIdList 权限 ID 列表
     * @return {@link Boolean}
     */
    Boolean deleteByPermissionIdList(List<Long> permissionIdList);

    /**
     * 删除角色与权限关系
     *
     * @param roleIds 角色id列表
     * @return {@link Boolean}
     */
    Boolean deleteByRoleIdList(Collection<Long> roleIds);

    /**
     * 保存角色权限关系
     *
     * @param roleId           角色id
     * @param permissionIdList 权限id列表
     * @return {@link Boolean}
     */
    Boolean saveRoleAndPermissionRelation(Long roleId, List<Long> permissionIdList);

    /**
     * 通过角色id，获取权限列表，返回一个树形结构
     *
     * @param roleId 角色id
     * @return {@link List}<{@link SysPermissionVo}>
     */
    List<SysPermissionVo> getPermissionTreeByRoleId(Long roleId);
}
