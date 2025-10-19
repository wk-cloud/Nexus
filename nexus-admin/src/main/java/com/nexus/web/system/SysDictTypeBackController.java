package com.nexus.web.system;


import com.nexus.common.annotation.OperationLog;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.core.validation.ValidGroup;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.OperationTypeEnum;
import com.nexus.system.domain.dto.SysDictTypeDto;
import com.nexus.system.domain.vo.SysDictTypeVo;
import com.nexus.system.service.SysDictTypeBackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 系统字典类型控制器
 *
 * @author wk
 * @date 2024/03/28
 */
@Tag(name = "系统字典类型后台模块")
@CrossOrigin
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeBackController {
    @Resource
    private SysDictTypeBackService sysDictTypeBackService;

    /**
     * 获取系统字典类型下拉选项数据
     *
     * @return {@link Result }<{@link List }<{@link Map }<{@link String }, {@link String }>>>
     */
    @Operation(summary = "获取系统字典类型下拉选项接口")
    @GetMapping("/options")
    public Result<List<Map<String, String>>> getSelectOptions(){
        return Result.success(sysDictTypeBackService.getSelectOptions());
    }

    /**
     * 获取系统字典数据
     *
     * @param dictType 字典类型
     * @return {@link Result }<{@link SysDictTypeVo }>
     */
    @Operation(summary = "获取系统字典数据接口")
    @GetMapping("/dictData/{dictType}")
    public Result<SysDictTypeVo> getSysDictData(@PathVariable("dictType") String dictType){
        return Result.success(sysDictTypeBackService.queryDictType(dictType));
    }

    /**
     * 获取系统字典类型
     *
     * @param id 系统字典类型id
     * @return {@link Result }<{@link SysDictTypeVo }>
     */
    @Operation(summary = "获取系统字典类型接口")
    @GetMapping("/{id}")
    public Result<SysDictTypeVo> getSysDictType(@PathVariable("id") Long id){
        return Result.success(sysDictTypeBackService.queryDictType(id));
    }

    /**
     * 获取系统字典类型分页列表
     *
     * @param sysDictTypeDto 系统字典类型
     * @param queryParams        查询参数
     * @return {@link Result }<{@link PagingData }<{@link SysDictTypeVo }>>
     */
    @Operation(summary = "获取系统字典类型分页列表接口")
    @GetMapping("/list")
    public Result<PagingData<SysDictTypeVo>> listSysDictType(SysDictTypeDto sysDictTypeDto, QueryParams queryParams) {
        return Result.success(sysDictTypeBackService.querySysDictTypeList(sysDictTypeDto, queryParams));
    }

    /**
     * 删除系统字典类型
     *
     * @param ids 系统字典类型id列表
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "删除系统字典类型接口")
    @OperationLog(operationDesc = "删除字典类型",operationType = OperationTypeEnum.DELETE)
    @RequiresRoles("admin")
    @DeleteMapping("/delete/{ids}")
    public Result<Void> deleteSysDictType(@PathVariable("ids") Long[] ids) {
        Boolean deleted = sysDictTypeBackService.deleteDictTypeByIds(Arrays.asList(ids));
        if(deleted){
            return Result.success();
        }
        return Result.fail("字典类型删除失败");
    }

    /**
     * 修改系统字典类型
     *
     * @param sysDictTypeDto 系统字典类型
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "修改系统字典类型接口")
    @OperationLog(operationDesc = "修改字典类型",operationType = OperationTypeEnum.UPDATE)
    @PutMapping("/update")
    @RequiresRoles("admin")
    public Result<Void> updateSysDictType(@Validated(ValidGroup.Update.class) @RequestBody SysDictTypeDto sysDictTypeDto) {
        boolean updated = sysDictTypeBackService.updateDictType(sysDictTypeDto);
        if (updated) {
            return Result.success();
        }
        return Result.fail("字典类型修改失败");
    }

    /**
     * 新增系统字典类型
     *
     * @param sysDictTypeDto 系统字典类型
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "新增系统字典类型接口")
    @OperationLog(operationDesc = "新增字典类型",operationType = OperationTypeEnum.ADD)
    @PostMapping("/add")
    @RequiresRoles("admin")
    public Result<Void> addSysDictType(@Validated(ValidGroup.Insert.class) @RequestBody SysDictTypeDto sysDictTypeDto) {
        boolean save = sysDictTypeBackService.addDictType(sysDictTypeDto);
        if (save) {
            return Result.success();
        }
        return Result.fail("字典类型保存失败");
    }

}
