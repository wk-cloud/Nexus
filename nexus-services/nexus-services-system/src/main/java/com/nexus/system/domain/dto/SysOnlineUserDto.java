package com.nexus.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户信息DTO
 *
 * @author wk
 * @date 2024/07/06
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "在线用户信息DTO", description = "在线用户信息DTO")
public class SysOnlineUserDto implements Serializable {

    @Schema(name = "主键id")
    private Long id;

    @Schema(name = "用户id")
    private Long userId;

    @Schema(name = "登录平台")
    private Integer loginPlatform;

    @Schema(name = "用户名")
    private String username;

    @Schema(name = "昵称")
    private String nickName;

    @Schema(name = "头像")
    private String avatar;

    @Schema(name = "登录时间")
    private LocalDateTime loginTime;

    @Schema(name = "登录类型")
    private Integer loginType;

    @Schema(name = "登录 IP")
    private String loginIp;

}
