package com.nexus.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 操作日志dto
 *
 * @author wk
 * @date 2023/03/11
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "操作日志DTO", description = "操作日志DTO")
public class SysOperationLogDto implements Serializable {

    @Schema(name = "主键id")
    private Long id;

    @Schema(name = "操作模块")
    private String module;

    @Schema(name = "操作类型")
    private String operationType;

    @Schema(name = "操作描述")
    private String operationDesc;

    @Schema(name = "请求类型")
    private String requestType;

    @Schema(name = "操作人员名称")
    private String username;

    @Schema(name = "操作人员id")
    private Long userId;

    @Schema(name = "操作人员ip")
    private String ip;

    @Schema(name = "请求路径")
    private String requestUrl;

    @Schema(name = "请求方法")
    private String requestMethod;

    @Schema(name = "请求参数")
    private String requestParams;

    @Schema(name = "请求结果")
    private String requestResult;
}
