package com.nexus.system.service;


import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.domain.dto.SysOnlineUserDto;
import com.nexus.system.domain.vo.SysOnlineUserVo;

/**
 * 在线用户服务
 *
 * @author wk
 * @date 2024/07/06
 */
public interface SysOnlineUserService extends IServicePlus<SysOnlineUser, SysOnlineUserVo> {

    /**
     * 保存在线用户
     *
     * @param onlineUser 在线用户
     * @return {@link Boolean}
     */
    Boolean saveOnlineUser(SysOnlineUser onlineUser);

    /**
     * 离线
     *
     * @param userId        用户 ID
     * @param loginPlatform 登录平台
     * @return {@link Boolean}
     */
    Boolean offline(Long userId,Integer loginPlatform);

    /**
     * 查询在线用户列表
     *
     * @param onlineUserBackDto 在线用户 DTO
     * @param queryParams       查询参数
     * @return {@link PagingData}<{@link SysOnlineUserVo}>
     */
    PagingData<SysOnlineUserVo> listOnlineUser(SysOnlineUserDto onlineUserBackDto, QueryParams queryParams);

    /**
     * 删除离线用户
     *
     * @param token 令 牌
     * @return {@link Boolean}
     */
    Boolean removeOfflineUser(String token);
}
