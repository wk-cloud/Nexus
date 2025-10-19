package com.nexus.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.nexus.common.core.ip.IpHome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志VO
 *
 * @author wk
 * @date 2024/06/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysLoginLogVo implements Serializable {

    /**
     * 主键id
     */
    @ExcelIgnore
    private Long id;

    /**
     * 用户 ID
     */
    @ExcelIgnore
    private Long userId;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名")
    private String username;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称")
    private String userNickName;

    /**
     * 登录类型
     */
    @ExcelProperty(value = "登录类型")
    private Integer loginType;

    /**
     * 登录平台
     */
    @ExcelProperty(value = "登录平台")
    private Integer loginPlatform;

    /**
     * 登录 IP
     */
    @ExcelProperty(value = "登录 IP")
    private String loginIp;

    /**
     * 登录 IP 归属
     */
    @ExcelIgnore
    private IpHome loginIpHome;

    /**
     * 浏览器名称
     */
    @ExcelProperty(value = "浏览器名称")
    private String browserName;

    /**
     * 操作系统名称
     */
    @ExcelProperty(value = "操作系统名称")
    private String osName;

    /**
     * 创建时间
     */
    @ExcelIgnore
    private LocalDateTime createTime;

    /**
     * 登录日期
     */
    @ExcelProperty(value = "登录时间")
    private LocalDateTime loginTime;

}
