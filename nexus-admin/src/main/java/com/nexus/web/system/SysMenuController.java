package com.nexus.web.system;

import com.nexus.common.core.domain.view.Result;
import com.nexus.common.core.enums.OperationTypeEnum;
import com.nexus.common.core.validation.ValidGroup;
import com.nexus.common.log.annotation.OperationLog;
import com.nexus.system.domain.dto.SysMenuDto;
import com.nexus.system.domain.vo.SysMenuVo;
import com.nexus.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理后台控制器
 * @author wk
 * @date 2022/9/27
 */
@Tag(name = "系统菜单管理模块")
@CrossOrigin
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "删除菜单接口")
    @OperationLog(operationDesc = "删除菜单",operationType = OperationTypeEnum.DELETE)
    @RequiresRoles("admin")
    @DeleteMapping("/delete/{menuId}")
    public Result<Void> deleteMenu(@PathVariable("menuId") Long menuId){
        Boolean deleted = sysMenuService.deleteMenu(menuId);
        if(deleted){
            return Result.success();
        }
        return Result.fail("菜单信息删除失败");
    }

    /**
     * 获取菜单
     *
     * @param menuId 菜单id
     * @return {@link Result }<{@link SysMenuVo }>
     */
    @Operation(summary = "获取菜单接口")
    @GetMapping("/{menuId}")
    public Result<SysMenuVo> getMenu(@PathVariable("menuId") Long menuId){
        return Result.success(sysMenuService.getMenuById(menuId));
    }

    /**
     * 获取所有菜单列表
     *
     * @return {@link Result }<{@link List }<{@link SysMenuVo }>>
     */
    @Operation(summary = "获取所有菜单列表接口")
    @GetMapping("/list/all")
    public Result<List<SysMenuVo>> getMenuList(){
        return Result.success(sysMenuService.queryVoList());
    }

    /**
     * 获取菜单列表
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Result }<{@link List }<{@link SysMenuVo }>>
     */
    @Operation(summary = "获取菜单列表接口")
    @GetMapping("/list")
    public Result<List<SysMenuVo>> listMenu(SysMenuDto sysMenuDto){
        return Result.success(sysMenuService.listMenu(sysMenuDto));
    }

    /**
     * 修改菜单
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "修改菜单接口")
    @OperationLog(operationDesc = "修改菜单",operationType = OperationTypeEnum.UPDATE)
    @RequiresRoles("admin")
    @PutMapping("/update")
    public Result<Void> updateMenu(@RequestBody @Validated(ValidGroup.Update.class) SysMenuDto sysMenuDto){
        Boolean updated = sysMenuService.updateMenu(sysMenuDto);
        if(updated){
            return Result.success();
        }
        return Result.fail("菜单更新失败");
    }

    /**
     * 新增菜单
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Result }
     */
    @Operation(summary = "新增菜单接口")
    @OperationLog(operationDesc = "新增菜单",operationType = OperationTypeEnum.ADD)
    @RequiresRoles("admin")
    @PostMapping("/add")
    public Result<Void> addMenu(@RequestBody @Validated(ValidGroup.Insert.class) SysMenuDto sysMenuDto){
        Boolean added = sysMenuService.addMenu(sysMenuDto);
        if(added){
            return Result.success();
        }
        return Result.fail("菜单新增失败");
    }
}
