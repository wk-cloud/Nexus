package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.enums.RoleEnum;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.SysRolePermission;
import com.nexus.system.domain.dto.SysRoleDto;
import com.nexus.system.domain.vo.SysPermissionVo;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.mapper.SysRoleMapper;
import com.nexus.system.service.SysPermissionService;
import com.nexus.system.service.SysRolePermissionService;
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
    private SysRolePermissionService rolePermissionService;
    @Resource
    private SysUserRoleService userRoleBackService;
    @Resource
    @Lazy
    private SysPermissionService sysPermissionService;

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
        userRoleBackService.deleteByRoleIdList(roleIds);
        // 删除角色和菜单的关系
        rolePermissionService.deleteByRoleIdList(roleIds);
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
        return ObjectUtils.isNotNull(role) ? role.getLabel() : StringUtils.emptyStr();
    }

    /**
     * 通过id获取角色
     *
     * @param roleId 角色id
     * @return {@link SysRoleVo}
     */
    @Override
    public SysRoleVo queryRoleById(Long roleId) {
        SysRoleVo roleBackVo = baseMapper.queryVoById(roleId);
        List<SysPermissionVo> permissionList = rolePermissionService.getPermissionTreeByRoleId(roleId);
        roleBackVo.setPermissionList(permissionList);
        return roleBackVo;
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
        List<Long> permissionIdList = sysRoleDto.getPermissionIdList();
        // 1. 先删除该角色之前的角色
        rolePermissionService.deleteByRoleId(role.getId());
        // 2. 添加新的角色权限关系
        rolePermissionService.saveRoleAndPermissionRelation(role.getId(), permissionIdList);
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
        // 保存角色和权限关系
        rolePermissionService.saveRoleAndPermissionRelation(role.getId(), sysRoleDto.getPermissionIdList());
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
        List<SysRoleVo> sysRoleVoList = pagingData.getDataList();
        if (CollectionUtils.isNotEmpty(sysRoleVoList)) {
            List<SysRolePermission> rolePermissionList = rolePermissionService.list();
            List<SysPermissionVo> permissionList = sysPermissionService.queryVoList();

            // 1. 角色和权限关系分组
            Map<Long, List<Long>> roleIdToPermissionIdsMap = rolePermissionList.stream()
                    .filter(rp -> rp.getRoleId() != null && rp.getPermissionId() != null)
                    .collect(Collectors.groupingBy(
                            SysRolePermission::getRoleId,
                            Collectors.mapping(SysRolePermission::getPermissionId, Collectors.toList())
                    ));

            // 2. 权限id和权限映射
            Map<Long, SysPermissionVo> permissionIdToVoMap = permissionList.stream()
                    .collect(Collectors.toMap(SysPermissionVo::getId, Function.identity()));

            // 3. 管理员标签
            String adminLabel = RoleEnum.ADMIN.getRoleLabel();

            // 4. 组装角色和权限
            for (SysRoleVo item : sysRoleVoList) {
                if (adminLabel.equals(item.getLabel())) {
                    item.setPermissionList(permissionList);
                    continue;
                }
                List<Long> permissionIds = roleIdToPermissionIdsMap.getOrDefault(item.getId(), Collections.emptyList());
                List<SysPermissionVo> assignedPermissions = permissionIds.stream()
                        .map(permissionIdToVoMap::get)
                        .filter(Objects::nonNull)
                        .toList();

                item.setPermissionList(assignedPermissions);
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
