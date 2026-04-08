package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.enums.AdminEnum;
import com.nexus.common.core.enums.PermissionStateEnum;
import com.nexus.common.core.utils.BeanUtils;
import com.nexus.common.core.utils.CollectionUtils;
import com.nexus.common.core.utils.TreeUtils;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.SysRoleMenu;
import com.nexus.system.domain.vo.SysMenuVo;
import com.nexus.system.mapper.SysRoleMenuMapper;
import com.nexus.system.service.SysMenuService;
import com.nexus.system.service.SysRoleMenuService;
import com.nexus.system.service.SysRoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 角色菜单服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Resource
    private SysRoleMenuMapper baseMapper;

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        return baseMapper.delete(lambdaQueryWrapper) > 0;
    }

    /**
     * 通过菜单id批量删除
     *
     * @param menuIdList 菜单 id 列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteByMenuIdList(List<Long> menuIdList) {
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysRoleMenu::getMenuId,menuIdList);
        return baseMapper.delete(lambdaQueryWrapper) > 0;
    }

    /**
     * 删除角色与菜单关系
     *
     * @param roleIds 角色id列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteByRoleIdList(Collection<Long> roleIds) {
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysRoleMenu::getRoleId,roleIds);
        return baseMapper.delete(lambdaQueryWrapper) > 0;
    }

    /**
     * 保存角色和菜单关系
     *
     * @param roleId      角色id
     * @param menuIdList 菜单id列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveRoleAndMenuRelation(Long roleId, List<Long> menuIdList) {
        List<SysRoleMenu> sysRoleMenuList = menuIdList.stream().map(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            return sysRoleMenu;
        }).collect(Collectors.toList());
        return super.saveBatch(sysRoleMenuList);
    }
}
