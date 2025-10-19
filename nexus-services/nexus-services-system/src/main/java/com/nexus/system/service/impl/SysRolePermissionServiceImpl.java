package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.enums.AdminEnum;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.TreeUtils;
import com.nexus.system.domain.SysPermission;
import com.nexus.system.domain.SysRolePermission;
import com.nexus.system.domain.vo.SysPermissionVo;
import com.nexus.system.mapper.SysRolePermissionMapper;
import com.nexus.system.service.SysPermissionService;
import com.nexus.system.service.SysRolePermissionService;
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
 * 角色权限服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Resource
    private SysRolePermissionMapper baseMapper;
    @Resource
    @Lazy
    private SysRoleService sysRoleService;
    @Resource
    @Lazy
    private SysPermissionService sysPermissionService;

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRolePermission::getRoleId, roleId);
        return this.remove(lambdaQueryWrapper);
    }

    /**
     * 通过权限id批量删除
     *
     * @param permissionIdList 权限 ID 列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteByPermissionIdList(List<Long> permissionIdList) {
        LambdaQueryWrapper<SysRolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysRolePermission::getPermissionId,permissionIdList);
        return this.remove(lambdaQueryWrapper);
    }

    /**
     * 删除角色与权限关系
     *
     * @param roleIds 角色id列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteByRoleIdList(Collection<Long> roleIds) {
        LambdaQueryWrapper<SysRolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysRolePermission::getRoleId,roleIds);
        return this.remove(lambdaQueryWrapper);
    }

    /**
     * 保存角色权限关系
     *
     * @param roleId           角色id
     * @param permissionIdList 权限id列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveRoleAndPermissionRelation(Long roleId, List<Long> permissionIdList) {
        // 1. 创建 list 集合，用于封装添加数据
        ArrayList<SysRolePermission> rolePermissionList = new ArrayList<>(permissionIdList.size());
        // 2. 遍历素有的菜单数组
        permissionIdList.forEach(permissionItem -> {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionItem);
            rolePermissionList.add(rolePermission);
        });
        return this.saveBatch(rolePermissionList);
    }

    /**
     * 根据角色id，获取角色对应的权限列表，返回的是一个树形结构
     *
     * @param roleId 角色id
     * @return {@link List}<{@link SysPermissionVo}>
     */
    @Override
    public List<SysPermissionVo> getPermissionTreeByRoleId(Long roleId) {
        String roleLabel = sysRoleService.queryRoleLabelById(roleId);
        if (AdminEnum.SUPER_ADMIN.getLabel().equals(roleLabel)) {
            return TreeUtils.createTree(sysPermissionService.queryVoList());
        }
        LambdaQueryWrapper<SysRolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionLambdaQueryWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> rolePermissionList = baseMapper.selectList(rolePermissionLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(rolePermissionList)){
            return Collections.emptyList();
        }
        // 查询所有的权限列表
        List<SysPermission> permissions = sysPermissionService.list();
        if(CollectionUtils.isEmpty(permissions)){
            return Collections.emptyList();
        }
        List<SysPermission> permissionList = permissions
                .stream()
                .filter(permission -> rolePermissionList.stream().anyMatch(rolePermission -> rolePermission.getPermissionId().equals(permission.getId())))
                .collect(Collectors.toList());
        List<SysPermissionVo> sysPermissionVoList = BeanUtils.copyToList(permissionList, SysPermissionVo.class);
        return TreeUtils.createTree(sysPermissionVoList);
    }
}
