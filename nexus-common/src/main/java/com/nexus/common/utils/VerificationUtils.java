package com.nexus.common.utils;


import com.nexus.common.enums.LoginTypeEnum;

/**
 * 校验工具类
 *
 * @author wk
 * @date 2024/05/29
 */
public class VerificationUtils {

    private static final String EMAIL_REG = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    private static final String PASSWORD_REG = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
    private static final String QQ_REG = "[1-9][0-9]{4,}";


    /**
     * 检查是否是合法的电子邮件
     *
     * @param email 电子邮件
     * @return {@link boolean}
     */
    public static boolean isEmail(String email){
        return email.matches(EMAIL_REG);
    }

    /**
     * 是否是合法的 qq 号码
     *
     * @param qq qq账号
     * @return {@link boolean}
     */
    public static boolean isQQ(String qq){
        return qq.matches(QQ_REG);
    }

    /**
     * 获取登录类型
     *
     * @param accountNumber 账号
     * @return {@link Integer}
     */
    public static Integer getLoginType(String accountNumber){
        Integer loginType = null;
        if(isEmail(accountNumber)){
            loginType = LoginTypeEnum.EMAIL.getCode();
        }else if(isQQ(accountNumber)){
            loginType = LoginTypeEnum.QQ.getCode();
        }
        return loginType;
    }
}
