package com.nexus.common.core.service;

/**
 * 在线用户服务
 *
 * @author wk
 * @date 2026/04/07
 */
public interface OnlineUserService {

    /**
     * 删除离线用户
     *
     * @param token 令 牌
     * @return {@link Boolean}
     */
    @SuppressWarnings("all")
    Boolean removeOfflineUser(String token);
}
