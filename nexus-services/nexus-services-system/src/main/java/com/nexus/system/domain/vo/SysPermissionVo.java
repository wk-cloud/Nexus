package com.nexus.system.domain.vo;

import com.nexus.common.core.base.TreeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限后台 VO
 *
 * @author wk
 * @date 2024/05/19
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysPermissionVo extends TreeEntity<SysPermissionVo> implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 组件名称
     */
    private String name;

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 重定向
     */
    private String redirect;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * 侧边菜单是否固定在tags-view中
     */
    private Boolean affix;

    /**
     * 一直显示根路由
     */
    private Boolean alwaysShow;

    /**
     * 组件缓存
     */
    private Boolean noCache;


    /**
     * 面包屑中是否显示
     */
    private Boolean breadcrumb;

    /**
     * 侧边栏高亮目标路由
     */
    private String activeMenu;

    /**
     * 菜单类型
     */
    private Integer type;

    /**
     * 菜单状态
     */
    private Integer state;

    /**
     * 层级，1：一级菜单，2：二级菜单
     */
    private Integer level = 1;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
