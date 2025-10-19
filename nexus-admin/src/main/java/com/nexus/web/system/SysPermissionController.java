package com.nexus.web.system;

import com.nexus.common.annotation.OperationLog;
import com.nexus.common.core.validation.ValidGroup;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.OperationTypeEnum;
import com.nexus.system.domain.dto.SysPermissionDto;
import com.nexus.system.domain.vo.SysPermissionVo;
import com.nexus.system.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理后台控制器
 * @author wk
 * @date 2022/9/27
 */
@Tag(name = "系统权限管理模块")
@CrossOrigin
@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {

    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 删除权限
     *
     * @param permissionId 权限id
     * @return {@link Result }<{@link Void }>
     */
    @Operation(description = "删除权限接口")
    @OperationLog(operationDesc = "删除权限",operationType = OperationTypeEnum.DELETE)
    @RequiresRoles("admin")
    @DeleteMapping("/delete/{permissionId}")
    public Result<Void> deletePermission(@PathVariable("permissionId") Long permissionId){
        Boolean deleted = sysPermissionService.deletePermission(permissionId);
        if(deleted){
            return Result.success();
        }
        return Result.fail("权限信息删除失败");
    }

    /**
     * 获取权限
     *
     * @param permissionId 权限id
     * @return {@link Result }<{@link SysPermissionVo }>
     */
    @Operation(description = "获取权限接口")
    @GetMapping("/{permissionId}")
    public Result<SysPermissionVo> getPermission(@PathVariable("permissionId") Long permissionId){
        return Result.success(sysPermissionService.getPermissionById(permissionId));
    }

    /**
     * 获取所有权限列表
     *
     * @return {@link Result }<{@link List }<{@link SysPermissionVo }>>
     */
    @Operation(description = "获取所有权限列表接口")
    @GetMapping("/list/all")
    public Result<List<SysPermissionVo>> getPermissionList(){
        return Result.success(sysPermissionService.queryVoList());
    }

    /**
     * 获取权限列表
     *
     * @param sysPermissionDto 系统权限
     * @return {@link Result }<{@link List }<{@link SysPermissionVo }>>
     */
    @Operation(description = "获取权限列表接口")
    @GetMapping("/list")
    public Result<List<SysPermissionVo>> listPermission(SysPermissionDto sysPermissionDto){
        return Result.success(sysPermissionService.listPermission(sysPermissionDto));
    }

    /**
     * 修改权限
     *
     * @param sysPermissionDto 系统权限
     * @return {@link Result }<{@link Void }>
     */
    @Operation(description = "修改权限接口")
    @OperationLog(operationDesc = "修改权限",operationType = OperationTypeEnum.UPDATE)
    @RequiresRoles("admin")
    @PutMapping("/update")
    public Result<Void> updatePermission(@RequestBody @Validated(ValidGroup.Update.class) SysPermissionDto sysPermissionDto){
        Boolean updated = sysPermissionService.updatePermission(sysPermissionDto);
        if(updated){
            return Result.success();
        }
        return Result.fail("权限更新失败");
    }

    /**
     * 新增权限
     *
     * @param sysPermissionDto 系统权限
     * @return {@link Result }
     */
    @Operation(description = "新增权限接口")
    @OperationLog(operationDesc = "新增权限",operationType = OperationTypeEnum.ADD)
    @RequiresRoles("admin")
    @PostMapping("/add")
    public Result<Void> addPermission(@RequestBody @Validated(ValidGroup.Insert.class) SysPermissionDto sysPermissionDto){
        Boolean added = sysPermissionService.addPermission(sysPermissionDto);
        if(added){
            return Result.success();
        }
        return Result.fail("权限新增失败");
    }
}
