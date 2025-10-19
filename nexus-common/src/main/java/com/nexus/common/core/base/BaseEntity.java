package com.nexus.common.core.base;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "基础实体类", description = "基础实体类")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {

    @Schema(name = "主键id")
    private Long id;

    @Schema(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(name = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
