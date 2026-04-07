package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.enums.AdminEnum;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    @Resource
    @Lazy
    private SysRoleService sysRoleService;
    @Resource
    @Lazy
    private SysMenuService sysMenuService;

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
        return this.remove(lambdaQueryWrapper);
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
        return this.remove(lambdaQueryWrapper);
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
        return this.remove(lambdaQueryWrapper);
    }

    /**
     * 保存角色和菜单关系
     *
     * @param roleId           角色id
     * @param menuIdList 菜单id列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveRoleAndMenuRelation(Long roleId, List<Long> menuIdList) {
        // 1. 创建 list 集合，用于封装添加数据
        ArrayList<SysRoleMenu> roleMenuList = new ArrayList<>(menuIdList.size());
        // 2. 遍历所有的菜单数组
        menuIdList.forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuList.add(roleMenu);
        });
        return this.saveBatch(roleMenuList);
    }

    /**
     * 根据角色id，获取角色对应的菜单列表，返回的是一个树形结构
     *
     * @param roleId 角色id
     * @return {@link List}<{@link SysMenuVo}>
     */
    @Override
    public List<SysMenuVo> getMenuTreeByRoleId(Long roleId) {
        String roleLabel = sysRoleService.queryRoleLabelById(roleId);
        if (AdminEnum.SUPER_ADMIN.getLabel().equals(roleLabel)) {
            return TreeUtils.createTree(sysMenuService.queryVoList());
        }
        LambdaQueryWrapper<SysRoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleMenuLambdaQueryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenuList = baseMapper.selectList(roleMenuLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(roleMenuList)){
            return Collections.emptyList();
        }
        // 查询所有的菜单列表
        List<SysMenu> menuList = sysMenuService.list();
        if(CollectionUtils.isEmpty(menuList)){
            return Collections.emptyList();
        }
        List<SysMenu> filteredMenuList = menuList
                .stream()
                .filter(menu -> roleMenuList.stream().anyMatch(roleMenu -> roleMenu.getMenuId().equals(menu.getId())))
                .collect(Collectors.toList());
        return TreeUtils.createTree(BeanUtils.copyToList(filteredMenuList, SysMenuVo.class));
    }
}
