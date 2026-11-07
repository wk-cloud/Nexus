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
 * 系统字典数据 DTO
 *
 * @author wk
 * @date 2024/05/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "系统字典数据DTO", description = "用来传递系统字典数据信息")
public class SysDictDataDto implements Serializable {

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @Schema(description = "字典标签")
    @NotBlank(message = "字典标签不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(min = 1, max = 25, message = "字典标签长度为1-25个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String dictLabel;

    @Schema(description = "字典键值")
    @NotBlank(message = "字典键值不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(min = 1, max = 25, message = "字典键值长度为1-25个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String dictValue;

    @Schema(description = "字典类型id")
    @NotNull(message = "字典类型id不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String dictType;

    @Schema(description = "字典排序")
    @NotNull(message = "字典排序不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private Integer dictSort;

    @Schema(description = "字典状态")
    @NotNull(message = "字典状态不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private Integer status;

    @Schema(description = "回显样式")
    private String listClass;

    @Schema(description = "备注")
    @Size(max = 250, message = "备注长度不能超过250字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String remark;

}
