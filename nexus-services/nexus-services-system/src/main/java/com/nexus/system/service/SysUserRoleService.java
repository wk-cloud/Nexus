package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.domain.vo.SysRoleVo;

import java.util.Collection;
import java.util.List;

/**
 * 用户和角色关联服务
 *
 * @author wk
 * @date 2024/02/03
 */
public interface SysUserRoleService extends IServicePlus<SysUserRole,SysUserRole> {

    /**
     * 通过角色 id 列表 删除用户和角色关系
     *
     * @param roleIds 角色 ID 列表
     * @return {@link Integer}
     */
    Integer deleteByRoleIdList(Collection<Long> roleIds);

    /**
     * 删除用户和角色关系
     *
     * @param UserId 用户ID
     * @return {@link Integer}
     */
    Integer deleteByUserId(Long UserId);

    /**
     * 为用户添加角色
     *
     * @param UserId 等待添加角色的用户id
     * @param roleNameList   角色名字列表
     */
    void addRoleForUser(Long UserId,List<String> roleNameList);

    /**
     * 获取角色列表
     *
     * @param UserId 用户基本id
     * @return {@link List}<{@link SysRoleVo}>
     */
    List<SysRoleVo> queryRoleListByUserId(Long UserId);

}
