package com.nexus.system.domain.vo;

import com.nexus.common.core.ip.IpHome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线用户VO
 *
 * @author wk
 * @date 2024/07/06
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysOnlineUserVo implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 登录平台
     */
    private Integer loginPlatform;

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
     * 登录ip归属
     */
    private IpHome loginIpHome;

    /**
     * 角色列表
     */
    private List<SysRoleVo> roleList = new ArrayList<>();

}
