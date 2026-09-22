package com.nexus.common.shiro.handler;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.nexus.common.core.domain.view.Result;
import com.nexus.common.core.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shiro 异常处理器
 *
 * @author wk
 * @date 2026/4/7 11:16
 */
@Slf4j
@Order(-1) // 因为全局异常处理与Shiro异常处理都有@ExceptionHandler注解，所以需要指定顺序，防止Shiro异常处理被全局异常处理拦截
@RestControllerAdvice
public class ShiroExceptionHandler {

    /**
     * 处理令牌失效异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler({SignatureVerificationException.class, AlgorithmMismatchException.class, TokenExpiredException.class, JWTDecodeException.class})
    public Result<Void> verificationOrAlgorithmMismatchExceptionHandle(Exception e) {
        log.error("====> token 异常", e);
        return Result.fail(HttpCodeEnum.TOKEN_EXPIRED.getCode(), "登录信息已经失效，请重新登录");
    }


    /**
     * 处理shiro异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public Result<Void> unAuthorizedExceptionHandle(Exception e) {
        log.error("====> Shiro 异常", e);
        return Result.fail("权限不足");
    }
}
