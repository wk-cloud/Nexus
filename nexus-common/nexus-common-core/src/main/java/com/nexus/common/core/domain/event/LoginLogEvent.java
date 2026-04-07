package com.nexus.common.core.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志事件
 *
 * @author wk
 * @date 2026/4/6 20:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginLogEvent implements Serializable {

    /**
     * 用户 id
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
