package com.nexus.common.core.view;

import com.nexus.common.enums.HttpCodeEnum;
import lombok.Data;

/**
 * 返回给前端结果的实体类
 *
 * @author wk
 * @date 2022/07/20
 */
@Data
public class Result<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应状态信息
     */
    private String message;

    /**
     * 返回给客户端的数据，T 类型
     */
    private T result;

    /**
     * 私有化构造器
     */
    private Result() {
    }

    /**
     * 创建成功响应结果
     *
     * @return Result<T>
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(HttpCodeEnum.SUCCESS.getCode());
        result.setMessage(HttpCodeEnum.SUCCESS.getInfo());
        return result;
    }

    /**
     * 创建带自定义消息的成功响应
     *
     * @param message 响应消息
     * @return Result<T>
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(HttpCodeEnum.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 创建带自定义状态码的成功响应
     *
     * @param code 状态码
     * @return Result<T>
     */
    public static <T> Result<T> success(Integer code) {
        Result<T> result = new Result<>();
        result.setCode(code);
        return result;
    }

    /**
     * 创建带自定义状态码和消息的成功响应
     *
     * @param code    状态码
     * @param message 响应消息
     * @return Result<T>
     */
    public static <T> Result<T> success(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 创建带数据的成功响应
     *
     * @param data 响应数据
     * @return Result<T>
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(HttpCodeEnum.SUCCESS.getCode());
        result.setMessage(HttpCodeEnum.SUCCESS.getInfo());
        result.setResult(data);
        return result;
    }


    /**
     * 创建带自定义状态码和数据的成功响应
     *
     * @param code 状态码
     * @param data 响应数据
     * @return Result<T>
     */
    public static <T> Result<T> success(Integer code, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(HttpCodeEnum.SUCCESS.getInfo());
        result.setResult(data);
        return result;
    }

    /**
     * 创建带自定义响应消息和数据的成功响应
     *
     * @param message 响应消息
     * @param data 响应数据
     * @return Result<T>
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(HttpCodeEnum.SUCCESS.getCode());
        result.setMessage(message);
        result.setResult(data);
        return result;
    }

    /**
     * 创建带自定义状态码和消息以及数据的成功响应
     *
     * @param code    状态码
     * @param message 响应消息
     * @param data 响应数据
     * @return Result<T>
     */
    public static <T> Result<T> success(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setResult(data);
        return result;
    }

    /**
     * 创建失败的响应结果
     *
     * @return Result<T>
     */
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(HttpCodeEnum.FAIL.getCode());
        result.setMessage(HttpCodeEnum.FAIL.getInfo());
        return result;
    }

    /**
     * 创建带自定义状态码的失败响应
     *
     * @param code 状态码
     * @return Result<T>
     */
    public static <T> Result<T> fail(Integer code) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(HttpCodeEnum.FAIL.getInfo());
        return result;
    }

    /**
     * 创建带自定义消息的失败响应
     *
     * @param message 响应消息
     * @return Result<T>
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(HttpCodeEnum.FAIL.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 创建带自定义状态码和消息的失败响应
     *
     * @param code    状态码
     * @param message 响应消息
     * @return Result<T>
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 设置响应数据
     * @param value 响应数据
     * @return Result<T>
     * @example Result<T> result = Result.<T>success().add(value);
     */
    public Result<T> add(T value) {
        this.result = value;
        return this;
    }
}
