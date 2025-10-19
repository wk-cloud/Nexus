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
import java.util.List;

/**
 * 角色 DTO
 *
 * @author wk
 * @date 2024/02/03
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "角色Dto", description = "角色Dto")
public class SysRoleDto implements Serializable {

    @Schema(name = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @NotBlank(message = "角色名称不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(message = "角色名称长度不能超过20", max = 20, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(name = "角色名称")
    private String name;

    @Schema(name = "角色标签")
    private String label;

    @Schema(name = "角色状态")
    private Integer state;

    @Schema(name = "菜单权限父子是否关联")
    private Boolean menuCheckStrictly;

    ;@Schema(name = "权限集合")
    private List<SysPermissionDto> permissionList;

    @Schema(name = "权限id集合")
    private List<Long> permissionIdList;
}
