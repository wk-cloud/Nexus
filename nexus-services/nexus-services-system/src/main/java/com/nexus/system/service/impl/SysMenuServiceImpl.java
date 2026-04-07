package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.utils.BeanUtils;
import com.nexus.common.core.utils.ObjectUtils;
import com.nexus.common.core.utils.StringUtils;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.dto.SysMenuDto;
import com.nexus.system.domain.vo.SysMenuVo;
import com.nexus.system.mapper.SysMenuMapper;
import com.nexus.system.service.SysMenuService;
import com.nexus.system.service.SysRoleMenuService;
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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper baseMapper;
    @Resource
    private SysRoleMenuService rolePermissionService;

    /**
     * @param permissionId 权限id
     * @return {@link SysMenuVo}
     */
    @Override
    public SysMenuVo getPermissionById(Long permissionId) {
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
        rolePermissionService.deleteByMenuIdList(idList);
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
        LambdaQueryWrapper<SysMenu> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionLambdaQueryWrapper.eq(SysMenu::getParentId, permissionId).select(SysMenu::getId);
        List<SysMenu> childrenIdList = baseMapper.selectList(permissionLambdaQueryWrapper);
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
     * @param sysMenuDto 系统权限
     * @return {@link Integer}
     */
    @Override
    public Boolean updatePermission(SysMenuDto sysMenuDto) {
        return this.updateById( BeanUtils.toBean(sysMenuDto, SysMenu.class));
    }

    /**
     * 添加权限
     *
     * @param sysMenuDto 系统权限
     * @return {@link Integer}
     */
    @Override
    public Boolean addPermission(SysMenuDto sysMenuDto) {
        return this.save(BeanUtils.toBean(sysMenuDto, SysMenu.class));
    }

    /**
     * 查询权限列表
     *
     * @param sysMenuDto 系统权限
     * @return {@link List}<{@link SysMenuVo}>
     */
    @Override
    public List<SysMenuVo> listPermission(SysMenuDto sysMenuDto) {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = this.buildLambdaQueryWrapper(sysMenuDto);
        return baseMapper.queryVoList(lambdaQueryWrapper);
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysMenuDto 系统权限
     * @return {@link LambdaQueryWrapper}<{@link SysMenu}>
     */
    private LambdaQueryWrapper<SysMenu> buildLambdaQueryWrapper(SysMenuDto sysMenuDto) {
        LambdaQueryWrapper<SysMenu> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysMenuDto)) {
            permissionLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysMenuDto.getId()), SysMenu::getId, sysMenuDto.getId());
            permissionLambdaQueryWrapper.like(StringUtils.isNotBlank(sysMenuDto.getName()), SysMenu::getName, sysMenuDto.getName());
            permissionLambdaQueryWrapper.like(StringUtils.isNotBlank(sysMenuDto.getTitle()), SysMenu::getTitle, sysMenuDto.getTitle());
            permissionLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysMenuDto.getState()), SysMenu::getState, sysMenuDto.getState());
        }
        return permissionLambdaQueryWrapper;
    }
}
