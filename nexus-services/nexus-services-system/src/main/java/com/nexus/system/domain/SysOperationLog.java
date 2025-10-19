package com.nexus.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 操作日志
 *
 * @author wk
 * @date  2022/11/5
 */
@TableName("sys_operation_log")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysOperationLog extends BaseEntity implements Serializable {

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

    /**
     * 删除标识
     */
    private Integer deleted;
}
