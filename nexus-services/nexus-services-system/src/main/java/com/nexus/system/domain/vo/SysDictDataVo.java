package com.nexus.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统字典数据vo
 *
 * @author wk
 * @date 2024/05/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysDictDataVo implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 字典类型 ID
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
     * 创建时间
     */
    private LocalDateTime createTime;
}
