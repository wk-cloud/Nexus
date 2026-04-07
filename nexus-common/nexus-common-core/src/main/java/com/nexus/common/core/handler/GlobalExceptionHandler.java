package com.nexus.common.core.handler;

import com.nexus.common.core.domain.view.Result;
import com.nexus.common.core.exception.LimitAccessException;
import com.nexus.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author wk
 * @date 2025/09/14
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理系统异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler({NullPointerException.class,RuntimeException.class})
    public Result<Void> systemExceptionHandle(Exception e){
        log.error("系统异常", e);
        return Result.fail("系统出现异常");
    }


    /**
     * 处理超出文件大小限制异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler(FileSizeLimitExceededException.class)
    public Result<Void> fileSizeLimitExceededExceptionHandle(Exception e){
        log.error("上传文件大小超过限制", e);
        return Result.fail("上传失败，失败原因：单次上传不能超过100MB");
    }


    /**
     * 处理参数校验异常
     *
     * @param bindException 绑定异常
     * @return {@link Result}
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> bindExceptionHandle(BindException bindException){
        FieldError fieldError = bindException.getFieldError();
        if (fieldError == null) {
            log.error("BindException 的 FieldError 为 null");
            return Result.fail("参数校验失败");
        }
        return Result.fail(fieldError.getDefaultMessage());
    }

    /**
     *  处理限制访问异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler(LimitAccessException.class)
    public Result<Void> limitAccessExceptionHandle(LimitAccessException e){
        log.error("限制访问", e);
        return Result.fail(e.getMessage());
    }

    /**
     * 处理服务异常
     *
     * @param e e
     * @return {@link Result}
     */
    @ExceptionHandler(ServiceException.class)
    public Result<Void> serviceExceptionHandle(ServiceException e){
        log.error("服务异常", e);
        return Result.fail(e.getCode(),e.getMessage());
    }

}
