package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.common.utils.TreeUtils;
import com.nexus.system.domain.SysPermission;
import com.nexus.system.domain.dto.SysPermissionDto;
import com.nexus.system.domain.vo.SysPermissionVo;
import com.nexus.system.mapper.SysPermissionMapper;
import com.nexus.system.service.SysPermissionService;
import com.nexus.system.service.SysRolePermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 权限服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Resource
    private SysPermissionMapper baseMapper;
    @Resource
    private SysRolePermissionService rolePermissionService;

    /**
     * @param permissionId 权限id
     * @return {@link SysPermissionVo}
     */
    @Override
    public SysPermissionVo getPermissionById(Long permissionId) {
        return baseMapper.queryVoById(permissionId);
    }

    /**
     * 删除权限
     *
     * @param permissionId 权限id
     * @return {@link Boolean}
     */
    @Override
    public Boolean deletePermission(Long permissionId) {
        // 1. 创建 list集合，用户封装所有删除菜单id值
        List<Long> idList = new ArrayList<>();
        // 2. 向 idList 集合设置删除菜单id
        this.queryPermissionChildrenById(permissionId, idList);
        // 把当前 id 封装到 idList里面
        idList.add(permissionId);
        // 删除角色和权限关系
        rolePermissionService.deleteByPermissionIdList(idList);
        return this.removeBatchByIds(idList);
    }

    /**
     * 通过子菜单id查询子菜单
     *
     * @param permissionId 权限id
     * @param idList       id列表
     */
    private void queryPermissionChildrenById(Long permissionId, List<Long> idList) {
        // 查询菜单里面子菜单id
        LambdaQueryWrapper<SysPermission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionLambdaQueryWrapper.eq(SysPermission::getParentId, permissionId).select(SysPermission::getId);
        List<SysPermission> childrenIdList = baseMapper.selectList(permissionLambdaQueryWrapper);
        // 把childrenIdList中的菜单id值获取出来，封装到idList中，做递归查询
        childrenIdList.forEach(item -> {
            // 封装到 idList 里面
            idList.add(item.getId());
            // 递归查询
            this.queryPermissionChildrenById(item.getId(), idList);
        });
    }

    /**
     * 更新权限
     *
     * @param sysPermissionDto 系统权限
     * @return {@link Integer}
     */
    @Override
    public Boolean updatePermission(SysPermissionDto sysPermissionDto) {
        return this.updateById( BeanUtils.toBean(sysPermissionDto, SysPermission.class));
    }

    /**
     * 添加权限
     *
     * @param sysPermissionDto 系统权限
     * @return {@link Integer}
     */
    @Override
    public Boolean addPermission(SysPermissionDto sysPermissionDto) {
        return this.save(BeanUtils.toBean(sysPermissionDto, SysPermission.class));
    }

    /**
     * 查询权限列表
     *
     * @param sysPermissionDto 系统权限
     * @return {@link List}<{@link SysPermissionVo}>
     */
    @Override
    public List<SysPermissionVo> listPermission(SysPermissionDto sysPermissionDto) {
        LambdaQueryWrapper<SysPermission> lambdaQueryWrapper = this.buildLambdaQueryWrapper(sysPermissionDto);
        return baseMapper.queryVoList(lambdaQueryWrapper);
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysPermissionDto 系统权限
     * @return {@link LambdaQueryWrapper}<{@link SysPermission}>
     */
    private LambdaQueryWrapper<SysPermission> buildLambdaQueryWrapper(SysPermissionDto sysPermissionDto) {
        LambdaQueryWrapper<SysPermission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysPermissionDto)) {
            permissionLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysPermissionDto.getId()), SysPermission::getId, sysPermissionDto.getId());
            permissionLambdaQueryWrapper.like(StringUtils.isNotBlank(sysPermissionDto.getName()), SysPermission::getName, sysPermissionDto.getName());
            permissionLambdaQueryWrapper.like(StringUtils.isNotBlank(sysPermissionDto.getTitle()), SysPermission::getTitle, sysPermissionDto.getTitle());
            permissionLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysPermissionDto.getState()), SysPermission::getState, sysPermissionDto.getState());
        }
        return permissionLambdaQueryWrapper;
    }
}
