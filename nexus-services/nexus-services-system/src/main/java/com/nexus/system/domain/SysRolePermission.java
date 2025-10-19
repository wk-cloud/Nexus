package com.nexus.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 角色权限关联实体
 *
 * @author wk
 * @date 角色权限关联实体
 */
@TableName("sys_role_permission")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysRolePermission extends BaseEntity implements Serializable {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限id
     */
    private Long permissionId;

    /**
     * 删除标识
     */
    private Integer deleted;
}
