package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.enums.AdminEnum;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.mapper.SysUserRoleMapper;
import com.nexus.system.service.SysRoleService;
import com.nexus.system.service.SysUserRoleService;
import com.nexus.system.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户和角色关联服务实现
 *
 * @author wk
 * @date 2024/02/03
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Resource
    private SysUserRoleMapper baseMapper;
    @Lazy
    @Resource
    private SysUserService sysUserService;
    @Lazy
    @Resource
    private SysRoleService sysRoleService;

    /**
     * 通过角色id删除用户和角色关系
     *
     * @param roleIds 角色 ID 列表
     * @return {@link Integer}
     */
    @Override
    public Integer deleteByRoleIdList(Collection<Long> roleIds) {
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysUserRole::getRoleId, roleIds);
        return baseMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 通过用户id删除用户和角色关系
     *
     * @param UserId 用户 ID
     * @return {@link Integer}
     */
    @Override
    public Integer deleteByUserId(Long UserId) {
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRole::getUserId, UserId);
        return baseMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 为用户添加角色
     *
     * @param userId        用户id
     * @param roleLabelList 角色标签列表
     */
    @Override
    public void addRoleForUser(Long userId, List<String> roleLabelList) {
        // 1.获取一下用户信息，判断是否是超级管理员
        String email = sysUserService.queryEmailByUserId(userId);
        if (AdminEnum.SUPER_ADMIN.getEmail().equals(email)) {
            throw new ServiceException("该用户是超级管理员，您的权限不足");
        }
        // 2.先删除用户和角色关系
        this.deleteByUserId(userId);

        // 3. 如果没有传入角色标签，则直接返回
        if (CollectionUtils.isEmpty(roleLabelList)) {
            return;
        }
        // 4. 保存用户和角色关系
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.select(SysRole::getId, SysRole::getLabel);
        List<SysRole> roleList = sysRoleService.list(roleLambdaQueryWrapper);
        roleList = roleList.stream().filter(role -> roleLabelList.contains(role.getLabel())).toList();
        if (CollectionUtils.isEmpty(roleList)) {
            return;
        }
        List<SysUserRole> sysUserRoleList = roleList.stream().map(role -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(role.getId());
            return sysUserRole;
        }).toList();
        this.saveBatch(sysUserRoleList);
    }

    /**
     * 通过用户id获取角色列表
     *
     * @param UserId 用户id
     * @return {@link List}<{@link SysRoleVo}>
     */
    @Override
    public List<SysRoleVo> queryRoleListByUserId(Long UserId) {
        LambdaQueryWrapper<SysUserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(SysUserRole::getUserId, UserId);
        List<SysUserRole> userRoleList = baseMapper.selectList(userRoleLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(userRoleList)) {
            return Collections.emptyList();
        }
        List<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).toList();
        return sysRoleService.queryVoListByIds(roleIdList);
    }
}
