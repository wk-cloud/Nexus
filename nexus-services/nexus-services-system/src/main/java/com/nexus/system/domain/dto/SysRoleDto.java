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

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @NotBlank(message = "角色名称不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(message = "角色名称长度不能超过20", max = 20, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标签")
    private String roleLabel;

    @Schema(description = "角色状态")
    private Integer status;

    @Schema(description = "菜单权限父子是否关联")
    private Boolean menuCheckStrictly;

    @Schema(description = "menuList", title = "菜单列表")
    private List<SysMenuDto> menuList;

    @Schema(description = "菜单id列表")
    private List<Long> menuIdList;
}
