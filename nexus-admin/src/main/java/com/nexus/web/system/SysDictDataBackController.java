package com.nexus.web.system;

import com.nexus.common.annotation.OperationLog;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.core.validation.ValidGroup;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.OperationTypeEnum;
import com.nexus.system.domain.dto.SysDictDataDto;
import com.nexus.system.domain.vo.SysDictDataVo;
import com.nexus.system.service.SysDictDataBackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * 系统字典数据后台控制器
 *
 * @author wk
 * @date 2024/03/28
 */
@Tag(name = "系统字典数据模块")
@CrossOrigin
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataBackController {
    @Resource
    private SysDictDataBackService sysDictDataBackService;

    /**
     * 获取系统字典数据分页列表
     *
     * @param sysDictDataDto 系统字典数据
     * @param queryParams        查询参数
     * @return {@link Result }<{@link PagingData }<{@link SysDictDataVo }>>
     */
    @Operation(summary = "获取系统字典数据分页列表接口")
    @GetMapping("/list")
    public Result<PagingData<SysDictDataVo>> listSysDictData(SysDictDataDto sysDictDataDto, QueryParams queryParams) {
        return Result.success(sysDictDataBackService.listSysDictData(sysDictDataDto, queryParams));
    }

    /**
     * 删除系统字典数据
     *
     * @param ids 系统字典数据id列表
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "删除系统字典数据接口")
    @OperationLog(operationDesc = "删除字典数据",operationType = OperationTypeEnum.DELETE)
    @RequiresRoles("admin")
    @DeleteMapping("/delete/{ids}")
    public Result<Void> deleteSysDictData(@PathVariable("ids") Long[] ids) {
        boolean deleted = sysDictDataBackService.removeBatchByIds(Collections.singletonList(ids));
        if (deleted) {
            return Result.success();
        }
        return Result.fail("字典数据删除失败");
    }

    /**
     * 修改系统字典数据
     *
     * @param sysDictDataDto 系统字典数据
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "修改系统字典数据接口")
    @OperationLog(operationDesc = "修改字典数据",operationType = OperationTypeEnum.UPDATE)
    @RequiresRoles("admin")
    @PutMapping("/update")
    public Result<Void> updateSysDictData(@RequestBody @Validated(ValidGroup.Update.class)  SysDictDataDto sysDictDataDto) {
        Boolean updated = sysDictDataBackService.updateSysDictData(sysDictDataDto);
        if (updated) {
            return Result.success();
        }
        return Result.fail("字典数据修改失败");
    }

    /**
     * 新增系统字典数据
     *
     * @param sysDictDataDto 系统字典数据
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "新增系统字典数据接口")
    @OperationLog(operationDesc = "新增字典数据",operationType = OperationTypeEnum.ADD)
    @RequiresRoles("admin")
    @PostMapping("/add")
    public Result<Void> addSysDictData(@RequestBody @Validated(ValidGroup.Insert.class)  SysDictDataDto sysDictDataDto) {
        Boolean saved = sysDictDataBackService.addSysDictData(sysDictDataDto);
        if (saved) {
            return Result.success();
        }
        return Result.fail("字典数据新增失败");
    }


}
