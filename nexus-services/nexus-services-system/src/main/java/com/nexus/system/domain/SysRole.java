package com.nexus.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 角色实体类
 *
 * @author wk
 * @date 2022/7/20
 */
@TableName("sys_role")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysRole extends BaseEntity implements Serializable {

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
}
