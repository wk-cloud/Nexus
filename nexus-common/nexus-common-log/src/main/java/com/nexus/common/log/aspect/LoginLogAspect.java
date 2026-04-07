package com.nexus.common.log.aspect;


import cn.hutool.http.useragent.UserAgent;
import com.nexus.common.core.domain.event.LoginLogEvent;
import com.nexus.common.core.domain.view.Result;
import com.nexus.common.core.utils.*;
import com.nexus.common.log.annotation.LoginLog;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.enums.HttpCodeEnum;
import com.nexus.common.token.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 登录日志切面
 *
 * @author wk
 * @date 2024/06/30
 */
@Aspect
@Slf4j
@Component
public class LoginLogAspect {
    @Around("@annotation(loginLog)")
    public Object loginLog(ProceedingJoinPoint proceedingJoinPoint, LoginLog loginLog) throws Throwable {
        // 环绕通知结果
        Result proceed = (Result) proceedingJoinPoint.proceed();
        if (!HttpCodeEnum.SUCCESS.getCode().equals(proceed.getCode())) {
            return proceed;
        }
        LoginVo loginVo = (LoginVo) proceed.getResult();
        // 获取token
        String token = loginVo.getToken();
        // 获取request
        HttpServletRequest request = SpringMvcUtils.getRequest();
        String userId = TokenUtils.getValueFromToken(token, "userId");
        Integer loginType = loginVo.getLoginType();
        if (ObjectUtils.isNull(loginType)) {
            loginType = loginLog.loginType().getCode();
        }
        // 记录日志
        UserAgent userAgent = UserAgentUtils.parse(request.getHeader("User-Agent"));
        String browserName = userAgent.getBrowser().getName();
        String platformName = userAgent.getPlatform().getName();
        String osVersion = userAgent.getOsVersion();
        // 保存日志信息
        LoginLogEvent login = new LoginLogEvent();
        login.setUserId(Long.parseLong(userId));
        login.setBrowserName(browserName);
        login.setOsName(platformName + osVersion);
        login.setLoginIp(IpUtils.getIpAddress(request));
        login.setLoginType(loginType);
        login.setLoginPlatform(loginLog.loginPlatform().getCode());
        login.setLoginTime(LocalDateTime.now());
        // 发送登录日志事件
        SpringUtils.context().publishEvent(login);
        return proceed;
    }
}
