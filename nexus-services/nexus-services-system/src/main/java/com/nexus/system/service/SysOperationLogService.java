package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysOperationLog;
import com.nexus.system.domain.vo.SysOperationLogVo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 博客日志服务
 *
 * @author wk
 * @date 2023/04/16
 */
public interface SysOperationLogService extends IServicePlus<SysOperationLog, SysOperationLogVo> {

    /**
     * 导出列表
     *
     * @param response      响应
     * @param blogLogIdList 博客日志 ID 列表
     */
    void exportList(List<Long> blogLogIdList, HttpServletResponse response);

    /**
     * 全部导出
     */
    void exportAll(HttpServletResponse response);

    /**
     * 查询博客日志列表
     *
     * @param operationLog     博客日志
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysOperationLogVo}>
     */
    PagingData<SysOperationLogVo> listBlogLog(SysOperationLog operationLog, QueryParams queryParams);
}
