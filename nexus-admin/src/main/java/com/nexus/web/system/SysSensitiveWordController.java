package com.nexus.web.system;


import com.nexus.common.annotation.Limit;
import com.nexus.common.annotation.Pass;
import com.nexus.common.core.helper.SensitiveWordHelper;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.LimitTypeEnum;
import com.nexus.common.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

/**
 * 敏感词控制器
 *
 * @author wk
 * @date 2025/07/21
 */
@Tag(name = "敏感词模块")
@CrossOrigin
@RestController
@RequestMapping("/system/sensitive")
public class SysSensitiveWordController {
    /**
     * 替换敏感词
     *
     * @param text       文本
     * @param replaceStr 替换字符串
     * @return {@link Result }<{@link String }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "替换敏感词接口")
    @GetMapping("/replace")
    public Result<String> replace(@RequestParam(required = false) String text, @RequestParam(required = false) String replaceStr) {
        String replaced = StringUtils.isBlank(replaceStr) ? SensitiveWordHelper.replace(text) : SensitiveWordHelper.replace(text, replaceStr);
        return Result.<String>success().add(replaced);
    }

    /**
     * 判断是否包含敏感词
     *
     * @param text 文本
     * @return {@link Result }<{@link Boolean }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "判断是否包含敏感词接口")
    @GetMapping("/contains")
    public Result<Boolean> contains(@RequestParam(required = false) String text) {
        return Result.success(SensitiveWordHelper.contains(text));
    }

    /**
     * 返回匹配到的第一个敏感词
     *
     * @param text 文本
     * @return {@link Result }<{@link String }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "返回匹配到的第一个敏感词接口")
    @GetMapping("/findFirst")
    public Result<String> findFirst(@RequestParam(required = false) String text) {
        return Result.<String>success().add(SensitiveWordHelper.findFirst(text));
    }

    /**
     * 返回匹配到的所有敏感词
     *
     * @param text 文本
     * @return {@link Result }<{@link Set }<{@link String }>>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "返回匹配到的所有敏感词接口")
    @GetMapping("/findAll")
    public Result<Set<String>> findAll(@RequestParam(required = false) String text) {
        return Result.success(SensitiveWordHelper.findAll(text));
    }
}
