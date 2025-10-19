package com.nexus.web.system;

import com.nexus.common.annotation.Pass;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.core.validation.ValidGroup;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.OperationTypeEnum;
import com.nexus.common.annotation.OperationLog;
import com.nexus.system.domain.dto.SysRoleDto;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色管理后台控制器
 *
 * @author wk
 * @date 2022/10/11
 */
@Tag(name = "角色管理模块")
@CrossOrigin
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 获取所有角色列表
     *
     * @return {@link Result }<{@link List }<{@link SysRoleVo }>>
     */
    @Operation(description = "获取所有角色列表接口")
    @GetMapping("/list/all")
    public Result<List<SysRoleVo>> getRoleListAll() {
        return Result.success(sysRoleService.queryRoleListAll());
    }

    /**
     * 删除角色
     *
     * @param ids 角色id列表
     * @return {@link Result }<{@link Void }>
     */
    @Operation(description = "删除角色接口")
    @OperationLog(operationDesc = "删除角色",operationType = OperationTypeEnum.DELETE)
    @RequiresRoles("admin")
    @DeleteMapping("/delete/{ids}")
    public Result<Void> deleteRole(@PathVariable("ids") Long[] ids) {
        Boolean deleted = sysRoleService.deleteRole(Arrays.asList(ids));
        if (deleted) {
            return Result.success();
        }
        return Result.fail("角色删除失败");
    }

    /**
     * 获取角色
     *
     * @param roleId 角色id
     * @return {@link Result }<{@link SysRoleVo }>
     */
    @Operation(description = "获取角色接口")
    @GetMapping("/{roleId}")
    public Result<SysRoleVo> getRole(@PathVariable("roleId") Long roleId) {
        return Result.success(sysRoleService.queryRoleById(roleId));
    }

    /**
     * 获取角色分页列表
     *
     * @param sysRoleDto  系统角色
     * @param queryParams 查询参数
     * @return {@link Result }<{@link PagingData }<{@link SysRoleVo }>>
     */
    @Operation(description = "获取角色分页列表接口")
    @GetMapping("/list")
    public Result<PagingData<SysRoleVo>> listRole(SysRoleDto sysRoleDto, QueryParams queryParams) {
        return Result.success(sysRoleService.listRole(sysRoleDto, queryParams));
    }

    /**
     * 修改角色
     *
     * @param sysRoleDto 系统角色
     * @return {@link Result }<{@link Void }>
     */
    @Operation(description = "修改角色信息接口")
    @OperationLog(operationDesc = "修改角色",operationType = OperationTypeEnum.UPDATE)
    @RequiresRoles("admin")
    @PutMapping("/update")
    public Result<Void> updateRole(@Validated(ValidGroup.Update.class) @RequestBody SysRoleDto sysRoleDto) {
        Boolean updated = sysRoleService.updateRole(sysRoleDto);
        if (updated) {
            return Result.success();
        }
        return Result.fail("角色修改失败");
    }

    @Operation(description = "新增角色接口")
    @OperationLog(operationDesc = "新增角色",operationType = OperationTypeEnum.ADD)
    @RequiresRoles("admin")
    @PostMapping("/add")
    public Result<Void> addRole(@Validated(ValidGroup.Insert.class) @RequestBody SysRoleDto sysRoleDto) {
        Boolean added = sysRoleService.addRole(sysRoleDto);
        if (added) {
            return Result.success();
        }
        return Result.fail("角色新增失败");
    }
}
