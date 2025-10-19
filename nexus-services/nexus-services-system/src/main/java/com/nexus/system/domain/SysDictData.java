package com.nexus.system.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统字典数据
 *
 * @author wk
 * @date 2024/03/26
 */
@TableName("sys_dict_data")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysDictData extends BaseEntity implements Serializable {

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 字典状态
     */
    private Integer state;

    /**
     * 回显样式
     */
    private String listClass;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标识
     */
    private Integer deleted;
}
