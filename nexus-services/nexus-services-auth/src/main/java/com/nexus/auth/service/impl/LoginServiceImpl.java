package com.nexus.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.auth.service.LoginService;
import com.nexus.auth.stragegy.LoginContext;
import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.helper.LoginHelper;
import com.nexus.common.utils.TokenUtils;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.mapper.SysOnlineUserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登录服务实现
 *
 * @author wk
 * @date 2025/04/05
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private SysOnlineUserMapper sysOnlineUserMapper;

    /**
     * 登录
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo }
     */
    @Override
    public LoginVo login(LoginDto loginDto) {
        return LoginContext.login(loginDto);
    }

    /**
     * 注销登录
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean loginOut() {
        // 获取当前登录用户的令牌
        String token = LoginHelper.getToken();
        // 清除在线用户信息
        LoginHelper.removeLoginUser();
        // 清除token
        TokenUtils.removeTokenFromRedisSet(token);
        // 清除在线状态
        return this.removeOfflineUser(token);
    }

    /**
     * 检查登录是否过期
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean checkLoginExpired() {
        String token = LoginHelper.getToken();
        boolean expired = TokenUtils.isExpired(token);
        if (expired) {
            LoginHelper.removeLoginUser();
            TokenUtils.removeTokenFromRedisSet(token);
            this.removeOfflineUser(token);
        }
        return expired;
    }

    /**
     * 删除离线用户
     *
     * @param token 令 牌
     * @return {@link Boolean }
     */
    private Boolean removeOfflineUser(String token){
        LambdaQueryWrapper<SysOnlineUser> onlineUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        onlineUserLambdaQueryWrapper.eq(SysOnlineUser::getLoginToken,token);
        return sysOnlineUserMapper.delete(onlineUserLambdaQueryWrapper) > 0;
    }
}
