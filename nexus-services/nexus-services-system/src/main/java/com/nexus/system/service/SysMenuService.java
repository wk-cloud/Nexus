package com.nexus.system.service;

import com.nexus.common.mybatisplus.core.mapper.IServicePlus;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.dto.SysMenuDto;
import com.nexus.system.domain.vo.SysMenuVo;

import java.util.List;

/**
 * 菜单服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysMenuService extends IServicePlus<SysMenu, SysMenuVo> {

    /**
     * 根据id，获取菜单
     * @param menuId 菜单id
     * @return {@link SysMenuVo}
     */
    SysMenuVo getMenuById(Long menuId);

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     * @return {@link Boolean}
     */
    Boolean deleteMenu(Long menuId);

    /**
     * 更新菜单
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Boolean}
     */
    Boolean updateMenu(SysMenuDto sysMenuDto);

    /**
     * 添加菜单
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Boolean}
     */
    Boolean addMenu(SysMenuDto sysMenuDto);

    /**
     * 查询菜单列表
     *
     * @param sysMenuDto 系统菜单
     * @return {@link List}<{@link SysMenuVo}>
     */
    List<SysMenuVo> listMenu(SysMenuDto sysMenuDto);
}
