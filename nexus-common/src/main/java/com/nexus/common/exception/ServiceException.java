package com.nexus.common.exception;


import com.nexus.common.enums.HttpCodeEnum;
import lombok.Getter;

import java.io.Serial;

/**
 * 服务异常处理
 *
 * @author wk
 * @date 2024/01/20
 */
public class ServiceException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 2258477558314498118L;

    /**
     * 状态码
     */
    @Getter
    private Integer code;

    /**
     * 异常消息
     */
    private String message;

    public ServiceException(){}

    public ServiceException(String message){
        this.code = HttpCodeEnum.FAIL.getCode();
        this.message = message;
    }

    public ServiceException(Integer code,String message)
    {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public ServiceException setMessage(String message)
    {
        this.message = message;
        return this;
    }
}
