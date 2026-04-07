package com.nexus.common.shiro.domain;


import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录 DTO
 *
 * @author wk
 * @date 2025/04/05
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDto {

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 登录平台
     */
    private Integer loginPlatform;

    /**
     * 第三方登录凭证
     * */
    private String code;

    /**
     * 验证码
     */
    private String verificationCode;

    /**
     * 验证码类型
     */
    private Integer verificationCodeType;

    /**
     * 验证数据(用于图形验证码校验)
     */
    private JSONObject verifyData;
}
