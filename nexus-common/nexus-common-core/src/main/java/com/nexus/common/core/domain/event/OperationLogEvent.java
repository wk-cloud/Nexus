package com.nexus.common.core.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * OperationLogEvent
 *
 * @author wk
 * @date 2026/4/7 16:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OperationLogEvent implements Serializable {

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 操作人员名称
     */
    private String username;

    /**
     * 操作人员id
     */
    private Long userId;

    /**
     * 操作人员ip
     */
    private String ip;

    /**
     * 请求url
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求结果
     */
    private String requestResult;

}
