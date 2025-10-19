package com.nexus.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.nexus.common.core.ip.IpHome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 操作日志
 *
 * @author wk
 * @date  2022/11/5
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysOperationLogVo implements Serializable {

    /**
     * 主键id
     */
    @ExcelIgnore
    private Long id;

    /**
     * 操作模块
     */
    @ExcelProperty("操作模块")
    private String module;

    /**
     * 操作类型
     */
    @ExcelProperty("操作类型")
    private String operationType;

    /**
     * 操作描述
     */
    @ExcelProperty("操作描述")
    private String operationDesc;

    /**
     * 请求方式
     */
    @ExcelProperty("请求类型")
    private String requestType;

    /**
     * 操作人员名称
     */
    @ExcelProperty("操作人员名称")
    private String username;

    /**
     * 操作人员id
     */
    @ExcelIgnore
    private Long userId;

    /**
     * 操作人员ip
     */
    @ExcelProperty("操作ip")
    private String ip;

    /**
     * 操作人员ip归属
     */
    @ExcelIgnore
    private IpHome ipHome = new IpHome();

    /**
     * 请求url
     */
    @ExcelProperty("请求路径")
    private String requestUrl;

    /**
     * 请求方法
     */
    @ExcelProperty("请求方法")
    private String requestMethod;

    /**
     * 请求参数
     */
    @ExcelIgnore
    private String requestParams;

    /**
     * 请求结果
     */
    @ExcelProperty("请求结果")
    private String requestResult;

    /**
     * 创建日期
     */
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
