package com.nexus.common.exception;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常句柄
 *
 * @author wk
 * @date 2025/09/14
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {


    /**
     * 处理令牌失效异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler({SignatureVerificationException.class, AlgorithmMismatchException.class, TokenExpiredException.class, JWTDecodeException.class})
    public Result verificationOrAlgorithmMismatchExceptionHandle(Exception e) {
        log.error(e.getMessage());
        return Result.fail(HttpCodeEnum.TOKEN_EXPIRED.getCode(), "登录信息已经失效，请重新登录");
    }


    /**
     * 处理shiro异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public Result unAuthorizedExceptionHandle(Exception e) {
        log.error(e.getMessage());
        return Result.fail("权限不足");
    }


    /**
     * 处理系统异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler({NullPointerException.class, RuntimeException.class})
    public Result systemExceptionHandle(Exception e) {
        log.error(e.getMessage());
        return Result.fail("系统出现异常");
    }


    /**
     * 处理超出文件大小限制异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler(FileSizeLimitExceededException.class)
    public Result fileSizeLimitExceededExceptionHandle(Exception e) {
        log.error(e.getMessage());
        return Result.fail("上传失败，失败原因：单次上传不能超过100MB");
    }


    /**
     * 处理参数校验异常
     *
     * @param bindException 绑定异常
     * @return {@link Result}
     */
    @ExceptionHandler(BindException.class)
    public Result bindExceptionHandle(BindException bindException) {
        FieldError fieldError = bindException.getFieldError();
        String defaultMessage = fieldError.getDefaultMessage();
        return Result.fail(defaultMessage);
    }


    /**
     * 处理限制访问异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler(LimitAccessException.class)
    public Result limitAccessExceptionHandle(LimitAccessException e) {
        log.error(e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 处理服务异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler(ServiceException.class)
    public Result<?> serviceExceptionHandle(ServiceException e) {
        log.error(e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

}
