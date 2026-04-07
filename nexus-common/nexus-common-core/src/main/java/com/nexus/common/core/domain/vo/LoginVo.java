package com.nexus.common.core.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录vo
 * @author wk
 * @date 2025/04/05
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginVo {

    /**
     * 令牌
     */
    private String token;

    /**
     * 是否登录标识
     */
    private Boolean loginFlag;

    /**
     * 登录类型
     */
    private Integer loginType;

}
