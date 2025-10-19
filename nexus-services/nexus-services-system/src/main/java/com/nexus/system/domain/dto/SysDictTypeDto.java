package com.nexus.system.domain.dto;

import com.nexus.common.core.validation.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统字典类型 后台 DTO
 *
 * @author wk
 * @date 2024/05/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "系统字典类型后台dto", description = "用来传递系统字典类型信息")
public class SysDictTypeDto implements Serializable {

    @Schema(name = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @Schema(name = "字典类型名称")
    @NotBlank(message = "字典类型名称不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(min = 1, max = 25, message = "字典类型名称长度为1-25个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String dictName;

    @Schema(name = "字典类型")
    @NotBlank(message = "字典类型不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(min = 1, max = 65, message = "字典类型长度为1-65个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String dictType;

    @Schema(name = "字典类型状态")
    private Integer state;

    @Schema(name = "备注")
    @Size(max = 250, message = "备注长度不能超过250个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String remark;

}
