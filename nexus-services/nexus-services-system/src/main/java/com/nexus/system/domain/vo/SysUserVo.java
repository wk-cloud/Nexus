package com.nexus.system.domain.vo;

import com.nexus.common.core.annotation.Desensitization;
import com.nexus.common.core.enums.DesensitizationTypeEnum;
import com.nexus.common.core.domain.ip.IpHome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * 角色信息列表
     */
    private List<SysRoleVo> roleList = new ArrayList<>();

    /**
     * 菜单信息列表
     */
    private List<SysMenuVo> menuList = new ArrayList<>();

    /**
     * 菜单权限(菜单权限标签)列表
     */
    private Set<String> menuPermissions = new HashSet<>();

    /**
     * 角色权限(角色标签)列表
     */
    private Set<String> rolePermissions = new HashSet<>();

}
