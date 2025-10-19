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
 * 权限后台 DTO
 *
 * @author wk
 * @date 2024/05/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "权限DTO", description = "权限DTO")
public class SysPermissionDto implements Serializable {

    @Schema(name = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @Schema(name = "父id")
    private Long parentId;

    @Schema(name = "组件名称")
    private String name;

    @NotBlank(message = "菜单名称不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(message = "菜单名称长度不能超过50个字符", max = 50, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(name = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;

    /**
     * 权限标识
     */
    private String perms;

    @Size(message = "路由访问地址长度不能超过255个字符", max = 255, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(name = "路由地址")
    private String path;

    @Schema(name = "重定向")
    private String redirect;

    @Schema(name = "排序")
    private Integer sort;

    @Size(message = "组件路径长度不能超过255个字符", max = 255, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(name = "组件路径")
    private String component;

    @Schema(name = "图标")
    private String icon;

    @Schema(name = "是否隐藏")
    private Boolean hidden;

    @Schema(name = "侧边菜单是否固定在tags-view中")
    private Boolean affix;

    @Schema(name = "一直显示根路由")
    private Boolean alwaysShow;

    @Schema(name = "组件缓存")
    private Boolean noCache;

    @Schema(name = "面包屑中是否显示")
    private Boolean breadcrumb;

    @Schema(name = "侧边栏高亮目标路由")
    private String activeMenu;

    @Schema(name = "菜单类型")
    private Integer type;

    @Schema(name = "菜单状态")
    private Integer state;
}
