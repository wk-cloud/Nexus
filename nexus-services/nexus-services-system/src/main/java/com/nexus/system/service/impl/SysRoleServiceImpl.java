package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.enums.AdminEnum;
import com.nexus.common.core.enums.PermissionStateEnum;
import com.nexus.common.core.enums.RoleEnum;
import com.nexus.common.core.exception.ServiceException;
import com.nexus.common.core.utils.*;
import com.nexus.common.mybatisplus.core.page.PagingData;
import com.nexus.common.mybatisplus.core.query.QueryParams;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.SysRoleMenu;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.domain.dto.SysRoleDto;
import com.nexus.system.domain.vo.SysMenuVo;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.mapper.SysRoleMapper;
import com.nexus.system.service.SysMenuService;
import com.nexus.system.service.SysRoleMenuService;
import com.nexus.system.service.SysRoleService;
import com.nexus.system.service.SysUserRoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 角色服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysRoleMapper baseMapper;
    @Resource
    private SysRoleMenuService roleMenuService;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    @Lazy
    private SysMenuService sysMenuService;

    /**
     * 获取角色列表
     *
     * @return {@link List}<{@link SysRoleVo}>
     */
    @Override
    public List<SysRoleVo> queryRoleListAll() {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.select(SysRole::getId, SysRole::getLabel, SysRole::getLabel);
        return baseMapper.queryVoList(roleLambdaQueryWrapper, SysRoleVo.class);
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色id集合
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteRole(Collection<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        // 删除角色和用户的关系
        sysUserRoleService.deleteByRoleIdList(roleIds);
        // 删除角色和菜单的关系
        roleMenuService.deleteByRoleIdList(roleIds);
        return baseMapper.deleteByIds(roleIds) > 0;
    }

    /**
     * 通过id获取角色标签
     *
     * @param roleId 角色id
     * @return {@link String}
     */
    @Override
    public String queryRoleLabelById(Long roleId) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(SysRole::getId, roleId).select(SysRole::getLabel);
        SysRole role = baseMapper.selectOne(roleLambdaQueryWrapper);
        return ObjectUtils.isNotNull(role) ? role.getLabel() : null;
    }

    /**
     * 通过id获取角色
     *
     * @param roleId 角色id
     * @return {@link SysRoleVo}
     */
    @Override
    public SysRoleVo queryRoleById(Long roleId) {
        SysRoleVo roleVo = baseMapper.queryVoById(roleId, SysRoleVo.class);
        if(ObjectUtils.isNull(roleVo)) {
            return null;
        }
        if (AdminEnum.SUPER_ADMIN.getLabel().equals(roleVo.getLabel())) {
            roleVo.setMenuTreeList(TreeUtils.createTree(sysMenuService.queryVoList()));
            return roleVo;
        }
        LambdaQueryWrapper<SysRoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleMenuLambdaQueryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenuList = roleMenuService.list(roleMenuLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(roleMenuList)){
            return roleVo;
        }
        Set<Long> menuIds = roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
        // 查询菜单列表
        LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        menuLambdaQueryWrapper.in(SysMenu::getId, menuIds).eq(SysMenu::getState, PermissionStateEnum.NORMAL.getCode());
        List<SysMenu> menuList = sysMenuService.list(menuLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(menuList)){
            return roleVo;
        }
        List<SysMenuVo> menuVoList = BeanUtils.copyToList(menuList, SysMenuVo.class);
        roleVo.setMenuTreeList(TreeUtils.createTree(menuVoList));
        return roleVo;
    }

    /**
     * 更新角色
     *
     * @param sysRoleDto 系统角色
     * @return {@link Integer}
     */
    @Override
    public Boolean updateRole(SysRoleDto sysRoleDto) {
        if (!this.checkRoleLabelUnique(sysRoleDto)) {
            throw new ServiceException("角色更新失败，角色【 " + sysRoleDto.getLabel() + " 】已经存在");
        }
        SysRole role = BeanUtils.toBean(sysRoleDto, SysRole.class);
        List<Long> menuIdList = sysRoleDto.getMenuIdList();
        // 1. 先删除该角色之前的角色
        roleMenuService.deleteByRoleId(role.getId());
        // 2. 添加新的角色菜单关系
        roleMenuService.saveRoleAndMenuRelation(role.getId(), menuIdList);
        // 3. 更新角色
        return this.updateById(role);
    }

    /**
     * 保存角色
     *
     * @param sysRoleDto 系统角色
     * @return {@link Integer}
     */
    @Override
    public Boolean addRole(SysRoleDto sysRoleDto) {
        if (!this.checkRoleLabelUnique(sysRoleDto)) {
            throw new ServiceException("角色添加失败，角色【 " + sysRoleDto.getLabel() + " 】已经存在");
        }
        SysRole role = BeanUtils.toBean(sysRoleDto, SysRole.class);
        // 保存角色
        boolean save = baseMapper.insert(role) > 0;
        // 保存角色和菜单关系
        roleMenuService.saveRoleAndMenuRelation(role.getId(), sysRoleDto.getMenuIdList());
        return save;
    }

    /**
     * 检查角色标签唯一性
     *
     * @param sysRoleDto 系统角色
     * @return {@link Boolean}
     */
    @Override
    public Boolean checkRoleLabelUnique(SysRoleDto sysRoleDto) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper
                .eq(SysRole::getLabel, sysRoleDto.getLabel())
                .ne(ObjectUtils.isNotNull(sysRoleDto.getId()), SysRole::getId, sysRoleDto.getId());
        return !baseMapper.exists(roleLambdaQueryWrapper);
    }

    /**
     * 查询角色列表
     *
     * @param sysRoleDto 系统角色
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysRoleVo}>
     */
    @Override
    public PagingData<SysRoleVo> listRole(SysRoleDto sysRoleDto, QueryParams queryParams) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = this.buildLambdaQueryWrapper(sysRoleDto);
        Page<SysRoleVo> roleBackVoPage = baseMapper.queryVoPage(queryParams.build(), roleLambdaQueryWrapper);
        PagingData<SysRoleVo> pagingData = PagingData.build(roleBackVoPage);
        List<SysRoleVo> roleList = pagingData.getDataList();
        if (CollectionUtils.isNotEmpty(roleList)) {
            Set<Long> roleIds = roleList.stream().map(SysRoleVo::getId).collect(Collectors.toSet());
            // 1. 获取角色和菜单关联列表
            LambdaQueryWrapper<SysRoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roleMenuLambdaQueryWrapper.in(SysRoleMenu::getRoleId, roleIds);
            List<SysRoleMenu> roleMenuList = roleMenuService.list(roleMenuLambdaQueryWrapper);

            // 2. 获取菜单列表
            List<SysMenuVo> menuList = sysMenuService.queryVoList();

            // 3. 角色和菜单关系分组
            Map<Long, List<Long>> roleIdToMenuIdsMap = roleMenuList.stream()
                    .filter(rp -> rp.getRoleId() != null && rp.getMenuId() != null)
                    .collect(Collectors.groupingBy(
                            SysRoleMenu::getRoleId,
                            Collectors.mapping(SysRoleMenu::getMenuId, Collectors.toList())
                    ));

            // 4. 菜单id和菜单映射
            Map<Long, SysMenuVo> menuIdToVoMap = menuList.stream()
                    .collect(Collectors.toMap(SysMenuVo::getId, Function.identity()));

            // 5. 管理员标签
            String adminLabel = RoleEnum.ADMIN.getRoleLabel();

            // 6. 组装角色和菜单
            for (SysRoleVo item : roleList) {
                if (adminLabel.equals(item.getLabel())) {
                    item.setMenuList(menuList);
                    continue;
                }
                List<Long> menuIds = roleIdToMenuIdsMap.getOrDefault(item.getId(), Collections.emptyList());
                List<SysMenuVo> assignedMenus = menuIds.stream()
                        .map(menuIdToVoMap::get)
                        .filter(Objects::nonNull)
                        .toList();

                item.setMenuList(assignedMenus);
            }
        }
        return pagingData;
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysRoleDto 系统角色
     * @return {@link LambdaQueryWrapper}<{@link SysRole}>
     */
    private LambdaQueryWrapper<SysRole> buildLambdaQueryWrapper(SysRoleDto sysRoleDto) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysRoleDto)) {
            roleLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysRoleDto.getId()), SysRole::getId, sysRoleDto.getId());
            roleLambdaQueryWrapper.like(StringUtils.isNotBlank(sysRoleDto.getName()), SysRole::getName, sysRoleDto.getName());
            roleLambdaQueryWrapper.like(StringUtils.isNotBlank(sysRoleDto.getLabel()), SysRole::getLabel, sysRoleDto.getLabel());
            roleLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysRoleDto.getState()), SysRole::getState, sysRoleDto.getState());
        }
        return roleLambdaQueryWrapper;
    }
}
