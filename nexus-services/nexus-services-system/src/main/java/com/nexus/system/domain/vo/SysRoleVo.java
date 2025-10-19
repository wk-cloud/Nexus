package com.nexus.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色后台 vo
 *
 * @author wk
 * @date 2024/05/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysRoleVo implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色标签
     */
    private String label;

    /**
     * 角色状态
     */
    private Integer state;

    /**
     * 菜单权限父子是否关联
     */
    private Boolean menuCheckStrictly;

    /**
     * 权限列表
     */
    private List<SysPermissionVo> permissionList = new ArrayList<>();

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
