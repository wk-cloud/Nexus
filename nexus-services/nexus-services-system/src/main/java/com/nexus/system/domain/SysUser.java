package com.nexus.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 用户基本信息实体类
 *
 * @author wk
 * @date 2022/7/19
 */
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysUser extends BaseEntity implements Serializable {

    /**
     * 关联父id
     */
    private Long parentId;

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
     * 用户密码
     */
    private String password;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 随机盐
     */
    private String salt;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 登录 IP
     */
    private String loginIp;

    /**
     * 第三方登录id
     */
    private String openid;

    /**
     * 是否禁用
     */
    private Boolean disabled;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}
