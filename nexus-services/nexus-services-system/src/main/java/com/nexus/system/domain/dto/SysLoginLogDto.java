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

    @Schema(name = "主键id")
    private Long id;

    @Schema(name ="用户 ID")
    private Long userId;

    @Schema(name ="登录类型")
    private Integer loginType;

    @Schema(name ="登录平台")
    private Integer loginPlatform;

    @Schema(name ="登录 IP")
    private String loginIp;

    @Schema(name ="浏览器名称")
    private String browserName;

    @Schema(name ="操作系统名称")
    private String osName;

}
