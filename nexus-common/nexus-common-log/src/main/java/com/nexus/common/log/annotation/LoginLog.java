package com.nexus.common.log.annotation;

import com.nexus.common.core.enums.LoginPlatformEnum;
import com.nexus.common.core.enums.LoginTypeEnum;

import java.lang.annotation.*;

/**
 * 登录日志
 *
 * @author wk
 * @date 2024/06/30
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoginLog {

    /**
     * 登录平台
     *
     * @return {@link LoginPlatformEnum}
     */
    LoginPlatformEnum loginPlatform() default LoginPlatformEnum.BACK_DESK;

    /**
     * 登录类型
     *
     * @return {@link LoginTypeEnum}
     */
    LoginTypeEnum loginType() default LoginTypeEnum.PASSWORD;

}
