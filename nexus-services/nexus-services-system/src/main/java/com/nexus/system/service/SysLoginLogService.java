package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysLoginLog;
import com.nexus.system.domain.dto.SysLoginLogDto;
import com.nexus.system.domain.vo.SysLoginLogVo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 登录日志服务
 *
 * @author wk
 * @date 2024/06/30
 */
public interface SysLoginLogService extends IServicePlus<SysLoginLog,SysLoginLogVo> {

    /**
     * 导出列表
     *
     * @param idList   ID 列表
     * @param response 响应
     */
    void exportList(List<Long> idList, HttpServletResponse response);

    /**
     * 全部导出
     *
     * @param response 响应
     */
    void exportAll(HttpServletResponse response);

    /**
     * 列出登录日志
     *
     * @param loginLogBackDto 登录日志DTO
     * @param queryParams     查询参数
     * @return {@link PagingData}<{@link SysLoginLogVo}>
     */
    PagingData<SysLoginLogVo> listLoginLog(SysLoginLogDto loginLogBackDto, QueryParams queryParams);
}
