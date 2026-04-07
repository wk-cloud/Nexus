package com.nexus.common.mybatisplus.core.domain;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基本实体
 *
 * @author wk
 * @date 2023/12/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
