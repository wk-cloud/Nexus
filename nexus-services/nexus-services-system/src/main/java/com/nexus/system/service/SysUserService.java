package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.dto.SysUserDto;
import com.nexus.system.domain.vo.SysUserVo;

import java.util.Map;

/**
 * 用户基本信息服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysUserService extends IServicePlus<SysUser, SysUserVo> {

    /**
     * 查询用户列表
     *
     * @param sysUserDto 系统用户
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysUserVo}>
     */
    PagingData<SysUserVo> listUser(SysUserDto sysUserDto, QueryParams queryParams);

    /**
     * 获取用户分布
     *
     * @return {@link Map}<{@link String},{@link Integer}>
     */
    Map<String, Integer> queryUserDistribution();

    /**
     * 通过用户id获取电子邮件
     *
     * @param userId 用户id
     * @return {@link String}
     */
    String queryEmailByUserId(Long userId);

    /**
     * 更新用户
     *
     * @param sysUserDto 系统用户
     * @return {@link Integer}
     */
    Boolean updateUser(SysUserDto sysUserDto);

    /**
     * 更新禁用状态
     *
     * @param userId  用户id
     * @param disable 禁用
     * @return {@link Boolean}
     */
    Boolean updateDisableState(Long userId, Boolean disable);

    /**
     * 更新密码
     *
     * @param olderPassword 旧密码
     * @param newPassword   新密码
     * @return {@link Boolean}
     */
    Boolean updatePassword(String olderPassword, String newPassword);

    /**
     * 通过用户基本id获取用户基本信息
     *
     * @param userId 用户id
     * @return {@link SysUserVo}
     */
    SysUserVo queryUserById(Long userId);

    /**
     * 验证电子邮件和密码
     *
     * @param email    电子邮件
     * @param password 密码
     * @return {@link SysUser}
     */
    SysUser verificationEmailAndPassword(String email, String password);

    /**
     * 按令牌查询用户信息
     *
     * @param token 令 牌
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> queryUserInfoByToken(String token);

    /**
     * 检查邮箱唯一性
     *
     * @param sysUserDto 系统用户
     * @return boolean
     */
    Boolean checkEmailUnique(SysUserDto sysUserDto);

}
