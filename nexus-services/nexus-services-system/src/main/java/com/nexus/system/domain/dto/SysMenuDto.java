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
 * 系统菜单 DTO
 *
 * @author wk
 * @date 2024/05/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "系统菜单DTO", description = "用来传递系统菜单信息")
public class SysMenuDto implements Serializable {

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @Schema(description = "父id")
    private Long parentId;

    @Schema(description = "组件名称")
    private String name;

    @NotBlank(message = "菜单名称不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Size(message = "菜单名称长度不能超过50个字符", max = 50, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(description = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;

    /**
     * 权限标识
     */
    @Size(message = "权限标识长度不能超过50个字符", max = 50, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(description = "权限标识")
    private String perms;

    @Size(message = "路由访问地址长度不能超过255个字符", max = 255, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "重定向")
    private String redirect;

    @Schema(description = "排序")
    private Integer sort;

    @Size(message = "组件路径长度不能超过255个字符", max = 255, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "是否隐藏")
    private Boolean hidden;

    @Schema(description = "侧边菜单是否固定在tags-view中")
    private Boolean affix;

    @Schema(description = "一直显示根路由")
    private Boolean alwaysShow;

    @Schema(description = "组件缓存")
    private Boolean noCache;

    @Schema(description = "面包屑中是否显示")
    private Boolean breadcrumb;

    @Schema(description = "侧边栏高亮目标路由")
    private String activeMenu;

    @Schema(description = "菜单类型")
    private Integer type;

    @Schema(description = "菜单状态")
    private Integer status;
}
