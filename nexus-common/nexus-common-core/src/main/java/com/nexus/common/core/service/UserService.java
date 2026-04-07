package com.nexus.common.core.service;

/**
 * 用户服务
 *
 * @author wk
 * @date 2026/04/07
 */
public interface UserService {

    /**
     * 根据用户id获取用户昵称
     *
     * @param userId 用户id
     * @return {@link String }
     */
    String getUserNickName(Long userId);

}
