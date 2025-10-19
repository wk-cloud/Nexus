package com.nexus.auth.service;


import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;

/**
 * 登录服务
 *
 * @author wk
 * @date 2025/04/05
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo }
     */
    LoginVo login(LoginDto loginDto);

    /**
     * 退出登录
     *
     * @return {@link Boolean}
     */
    Boolean loginOut();

    /**
     * 检查登录是否过期
     *
     * @return {@link Boolean}
     */
    Boolean checkLoginExpired();
}
