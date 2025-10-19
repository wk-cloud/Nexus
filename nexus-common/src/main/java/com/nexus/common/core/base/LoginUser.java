package com.nexus.common.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录用户
 *
 * @author wk
 * @date 2025/03/27
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUser extends BaseEntity implements Serializable {

    /**
     * 关联父id
     */
    private Long pid;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 令 牌
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个人网站地址
     */
    private String website;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 登录 IP
     */
    private String loginIp;

    /**
     * 第三方登录id
     */
    private String openid;
}
