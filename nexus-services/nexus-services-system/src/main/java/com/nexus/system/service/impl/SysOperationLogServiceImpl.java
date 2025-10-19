package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.*;
import com.nexus.system.domain.SysOperationLog;
import com.nexus.system.domain.vo.SysOperationLogVo;
import com.nexus.system.mapper.SysOperationLogMapper;
import com.nexus.system.service.SysOperationLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * 博客日志服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    @Resource
    private SysOperationLogMapper baseMapper;


    /**
     * 导出列表
     *
     * @param response      响应
     * @param blogLogIdList 博客日志 ID 列表
     */
    @Override
    public void exportList(List<Long> blogLogIdList, HttpServletResponse response) {
        try {
            String fileName = "blogLog" + DateUtils.getYearMonthDay(new Date()) + ".xlsx";
            String sheetName = "操作日志";
            ExcelUtils.export(response, this.queryExportBlogLogList(blogLogIdList), SysOperationLogVo.class, fileName, sheetName);
        } catch (Exception e) {
            throw new ServiceException("日志导出失败,失败原因：" + e.getMessage());
        }
    }

    /**
     * 全部导出
     */
    @Override
    public void exportAll(HttpServletResponse response) {
        try {
            String fileName = "blogLog" + DateUtils.getYearMonthDay(new Date()) + ".xlsx";
            String sheetName = "操作日志";
            ExcelUtils.export(response, this.queryAllExportBlogLog(), SysOperationLogVo.class, fileName, sheetName);
        } catch (Exception e) {
            throw new ServiceException("日志导出失败,失败原因：" + e.getMessage());
        }
    }

    /**
     * 获取导出博客日志列表
     *
     * @param blogLogIdList 博客日志id列表
     * @return {@link List}<{@link SysOperationLogVo}>
     */
    private List<SysOperationLogVo> queryExportBlogLogList(List<Long> blogLogIdList) {
        LambdaQueryWrapper<SysOperationLog> blogLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        blogLogLambdaQueryWrapper.in(SysOperationLog::getId,blogLogIdList);
        return baseMapper.queryVoList(blogLogLambdaQueryWrapper, SysOperationLogVo.class);
    }

    /**
     * 获取所有导出的博客日志
     *
     * @return {@link List}<{@link SysOperationLogVo}>
     */
    private List<SysOperationLogVo> queryAllExportBlogLog() {
        return baseMapper.queryVoList(new LambdaQueryWrapper<>(), SysOperationLogVo.class);
    }

    /**
     * 查询博客日志列表
     *
     * @param operationLog     博客日志
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysOperationLogVo}>
     */
    @Override
    public PagingData<SysOperationLogVo> listBlogLog(SysOperationLog operationLog, QueryParams queryParams) {
        LambdaQueryWrapper<SysOperationLog> blogLogLambdaQueryWrapper = this.buildLambdaQueryWrapper(operationLog);
        blogLogLambdaQueryWrapper.orderByDesc(SysOperationLog::getCreateTime);
        Page<SysOperationLogVo> operationLogBackVoPage = baseMapper.queryVoPage(queryParams.build(), blogLogLambdaQueryWrapper, SysOperationLogVo.class);
        PagingData<SysOperationLogVo> pagingData = PagingData.build(operationLogBackVoPage);
        List<SysOperationLogVo> operationLogBackVoList = pagingData.getDataList();
        operationLogBackVoList.forEach(log -> {
            log.setIpHome(IpUtils.completeIpHome(log.getIp()));
        });
        return pagingData;
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param operationLog 操作日志
     * @return {@link LambdaQueryWrapper}<{@link SysOperationLog}>
     */
    private LambdaQueryWrapper<SysOperationLog> buildLambdaQueryWrapper(SysOperationLog operationLog) {
        LambdaQueryWrapper<SysOperationLog> blogLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(operationLog)) {
            blogLogLambdaQueryWrapper.eq(ObjectUtils.isNotNull(operationLog.getId()), SysOperationLog::getId, operationLog.getId());
            blogLogLambdaQueryWrapper.eq(StringUtils.isNotBlank(operationLog.getIp()), SysOperationLog::getIp, operationLog.getIp());
            blogLogLambdaQueryWrapper.like(StringUtils.isNotBlank(operationLog.getOperationDesc()), SysOperationLog::getOperationDesc, operationLog.getOperationDesc());
            blogLogLambdaQueryWrapper.like(StringUtils.isNotBlank(operationLog.getRequestMethod()), SysOperationLog::getRequestMethod, operationLog.getRequestMethod());
            blogLogLambdaQueryWrapper.like(StringUtils.isNotBlank(operationLog.getRequestParams()), SysOperationLog::getRequestParams, operationLog.getRequestParams());
            blogLogLambdaQueryWrapper.like(StringUtils.isNotBlank(operationLog.getModule()), SysOperationLog::getModule, operationLog.getModule());
            blogLogLambdaQueryWrapper.eq(StringUtils.isNotBlank(operationLog.getRequestType()), SysOperationLog::getRequestType, operationLog.getRequestType());
            blogLogLambdaQueryWrapper.like(StringUtils.isNotBlank(operationLog.getRequestUrl()), SysOperationLog::getRequestUrl, operationLog.getRequestUrl());
            blogLogLambdaQueryWrapper.eq(StringUtils.isNotBlank(operationLog.getOperationType()), SysOperationLog::getOperationType, operationLog.getOperationType());
        }
        return blogLogLambdaQueryWrapper;
    }
}
