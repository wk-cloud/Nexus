package com.nexus.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 系统字典类型vo
 *
 * @author wk
 * @date 2024/05/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysDictTypeVo implements Serializable {

    /**
     * 主键id
     */
    private Long id;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 系统字典数据
     */
    private List<SysDictDataVo> dictDataList = new ArrayList<>();

}
