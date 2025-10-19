package com.nexus.auth.stragegy;

import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.ObjectUtils;

import java.util.concurrent.ConcurrentMap;

/**
 * 登录上下文
 *
 * @author wk
 * @date 2025/04/05
 */
public class LoginContext {
    /**
     * 登录策略映射
     */
    private static final ConcurrentMap<Integer, AbstractLoginStrategy> loginStrategyMap = AbstractLoginStrategy.loginStrategyMap;

    /**
     * 登录
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo }
     */
    public static LoginVo login(LoginDto loginDto) {
        AbstractLoginStrategy loginStrategy = loginStrategyMap.get(loginDto.getLoginType());
        if (ObjectUtils.isNull(loginStrategy)) {
            throw new ServiceException("登录类型错误");
        }
        return loginStrategy.login(loginDto);
    }
}
