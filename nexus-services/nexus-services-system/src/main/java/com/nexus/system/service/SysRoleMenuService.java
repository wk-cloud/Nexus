package com.nexus.system.service;

import com.nexus.common.mybatisplus.core.mapper.IServicePlus;
import com.nexus.system.domain.SysRoleMenu;
import com.nexus.system.domain.vo.SysMenuVo;

import java.util.Collection;
import java.util.List;

/**
 * 角色菜单服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysRoleMenuService extends IServicePlus<SysRoleMenu, SysRoleMenu> {

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     * @return {@link Boolean}
     */
    Boolean deleteByRoleId(Long roleId);

    /**
     * 通过菜单id批量删除
     *
     * @param menuIdList 菜单 ID 列表
     * @return {@link Boolean}
     */
    Boolean deleteByMenuIdList(List<Long> menuIdList);

    /**
     * 删除角色与菜单关系
     *
     * @param roleIds 角色id列表
     * @return {@link Boolean}
     */
    Boolean deleteByRoleIdList(Collection<Long> roleIds);

    /**
     * 保存角色菜单关系
     *
     * @param roleId           角色id
     * @param menuIdList 菜单id列表
     * @return {@link Boolean}
     */
    Boolean saveRoleAndMenuRelation(Long roleId, List<Long> menuIdList);

    /**
     * 通过角色id，获取菜单树列表
     *
     * @param roleId 角色id
     * @return {@link List}<{@link SysMenuVo}>
     */
    List<SysMenuVo> getMenuTreeByRoleId(Long roleId);
}
