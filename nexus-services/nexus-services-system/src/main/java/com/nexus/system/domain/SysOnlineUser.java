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
 * 在线用户
 *
 * @author wk
 * @date 2024/07/06
 */
@TableName("t_online_user")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysOnlineUser extends BaseEntity implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 登录平台
     */
    private Integer loginPlatform;

    /**
     * 登录令牌
     */
    private String loginToken;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 删除
     */
    private Integer deleted;

}
