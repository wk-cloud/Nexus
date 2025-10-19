package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.dto.SysRoleDto;
import com.nexus.system.domain.vo.SysRoleVo;

import java.util.Collection;
import java.util.List;

/**
 * 角色服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysRoleService extends IServicePlus<SysRole, SysRoleVo> {

    /**
     * 获取角色列表
     *
     * @return {@link List}<{@link SysRoleVo}>
     */
    List<SysRoleVo> queryRoleListAll();

    /**
     * 删除角色
     *
     * @param roleIds 角色id集合
     * @return {@link Boolean}
     */
    Boolean deleteRole(Collection<Long> roleIds);

    /**
     * 更新角色
     *
     * @param sysRoleDto 系统角色
     * @return {@link Boolean}
     */
    Boolean updateRole(SysRoleDto sysRoleDto);

    /**
     * 通过id获取角色名
     *
     * @param roleId 角色id
     * @return {@link String}
     */
    String queryRoleLabelById(Long roleId);

    /**
     * 通过id获取角色
     *
     * @param roleId 角色id
     * @return {@link SysRoleVo}
     */
    SysRoleVo queryRoleById(Long roleId);

    /**
     * 保存角色
     *
     * @param sysRoleDto 系统角色
     * @return {@link Integer}
     */
    Boolean addRole(SysRoleDto sysRoleDto);

    /**
     * 检查角色标签唯一性
     *
     * @param sysRoleDto 系统角色
     * @return {@link Boolean}
     */
    Boolean checkRoleLabelUnique(SysRoleDto sysRoleDto);

    /**
     * 查询角色列表
     *
     * @param sysRoleDto 系统角色
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysRoleVo}>
     */
    PagingData<SysRoleVo> listRole(SysRoleDto sysRoleDto, QueryParams queryParams);
}
