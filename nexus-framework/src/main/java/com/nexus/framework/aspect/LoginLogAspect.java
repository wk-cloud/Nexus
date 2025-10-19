package com.nexus.framework.aspect;


import cn.hutool.http.useragent.UserAgent;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.helper.LoginHelper;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.HttpCodeEnum;
import com.nexus.common.utils.IpUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.TokenUtils;
import com.nexus.common.utils.UserAgentUtils;
import com.nexus.common.annotation.LoginLog;
import com.nexus.system.domain.SysLoginLog;
import com.nexus.system.service.SysLoginLogService;
import jakarta.annotation.Resource;
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

    @Resource
    private SysLoginLogService sysLoginLogService;

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
        HttpServletRequest request = LoginHelper.getRequest();
        String userId = (String) TokenUtils.getValueFromToken(token, "userId");
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
        SysLoginLog login = new SysLoginLog();
        login.setUserId(Long.parseLong(userId));
        login.setBrowserName(browserName);
        login.setOsName(platformName + osVersion);
        login.setLoginIp(IpUtils.getIpAddress(request));
        login.setLoginType(loginType);
        login.setLoginPlatform(loginLog.loginPlatform().getCode());
        login.setLoginTime(LocalDateTime.now());
        sysLoginLogService.save(login);
        return proceed;
    }
}
