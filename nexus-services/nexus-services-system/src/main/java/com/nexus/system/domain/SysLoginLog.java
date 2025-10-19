package com.nexus.system.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志
 *
 * @author wk
 * @date 2024/06/30
 */
@TableName("t_login_log")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysLoginLog extends BaseEntity implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 登录平台
     */
    private Integer loginPlatform;

    /**
     * 登录 IP
     */
    private String loginIp;

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

}
