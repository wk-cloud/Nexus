package com.nexus.system.domain.vo;

import com.nexus.common.annotation.Desensitization;
import com.nexus.common.core.ip.IpHome;
import com.nexus.common.enums.DesensitizationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息后台 VO
 *
 * @author wk
 * @date 2023/12/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysUserVo implements Serializable {

    /**
     * 主键id
     */
    private Long id;

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
     * 用户邮箱
     */
    @Desensitization(desensitizationType = DesensitizationTypeEnum.EMAIL)
    private String email;

    /**
     * 登录日期
     */
    private LocalDateTime loginTime;

    /**
     * 创建时间(注册时间)
     */
    private LocalDateTime createTime;

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
     * 禁用状态
     */
    private Boolean disabled;

    /**
     * 角色列表
     */
    private List<SysRoleVo> roleList = new ArrayList<>();

}
