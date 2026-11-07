package com.nexus.system.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录日志 DTO
 *
 * @author wk
 * @date 2024/06/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "登录日志DTO", description = "登录日志DTO")
public class SysLoginLogDto implements Serializable {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description ="用户id")
    private Long userId;

    @Schema(description ="登录类型")
    private Integer loginType;

    @Schema(description ="登录平台")
    private Integer loginPlatform;

    @Schema(description ="登录ip")
    private String loginIp;

    @Schema(description ="浏览器名称")
    private String browserName;

    @Schema(description ="操作系统名称")
    private String osName;

}
