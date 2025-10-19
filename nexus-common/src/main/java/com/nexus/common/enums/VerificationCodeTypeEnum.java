package com.nexus.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型枚举
 *
 * @author wk
 * @date 2024/05/29
 */
@Getter
@AllArgsConstructor
public enum VerificationCodeTypeEnum {

    // 前台登录状态验证码
    LOGIN_FRONT_STATE_CODE(1,"前台登录状态验证码","login:front:code:"),
    // 后台登录状态验证码
    LOGIN_BACK_STATE_CODE(2,"后台登录状态验证码","login:back:code:"),
    // 重置密码状态验证码
    RESET_PASSWORD_STATE_CODE(3,"重置密码状态验证码","reset:password:front:code:"),
    // 注册状态验证码
    REGISTER_STATE_CODE(4,"注册状态验证码","register:front:code:"),
    // 邮箱绑定状态验证码
    EMAIL_BINDING_STATE_CODE(5,"邮箱绑定状态验证码","binding:email:front:code:");

    private final Integer code;

    private final String info;

    private final String key;

    /**
     * 获取密钥
     *
     * @param type 类型
     * @return {@link String}
     */
    public static String getKey(Integer type){
        VerificationCodeTypeEnum[] verificationCodeTypeEnums = VerificationCodeTypeEnum.values();
        for (VerificationCodeTypeEnum verificationCodeTypeEnum : verificationCodeTypeEnums) {
            if(verificationCodeTypeEnum.getCode().equals(type)){
                return verificationCodeTypeEnum.getKey();
            }
        }
        return null;
    }
}
