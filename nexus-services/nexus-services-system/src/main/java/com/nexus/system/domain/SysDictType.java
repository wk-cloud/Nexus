package com.nexus.system.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统字典类型实体
 *
 * @author wk
 * @date 2024/03/28
 */
@TableName("sys_dict_type")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysDictType extends BaseEntity implements Serializable {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典类型状态
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标识
     */
    private Integer deleted;

}
