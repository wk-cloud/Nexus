package com.nexus.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.ip.IpHome;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.*;
import com.nexus.system.domain.SysLoginLog;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.dto.SysLoginLogDto;
import com.nexus.system.domain.vo.SysLoginLogVo;
import com.nexus.system.mapper.SysLoginLogMapper;
import com.nexus.system.service.SysLoginLogService;
import com.nexus.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现
 *
 * @author wk
 * @date 2024/06/30
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {
    @Resource
    private SysLoginLogMapper baseMapper;
    @Resource
    private SysUserService sysUserService;


    /**
     * 导出列表
     *
     * @param idList   ID 列表
     * @param response 响应
     */
    @Override
    public void exportList(List<Long> idList, HttpServletResponse response) {
        try {
            String fileName = "loginLog" + DateUtils.getYearMonthDay(new Date()) + ".xlsx";
            String sheetName = "登录日志";
            ExcelUtils.export(response, this.queryLoginLogList(idList), SysLoginLogVo.class, fileName, sheetName);
        } catch (Exception e) {
            throw new ServiceException("日志导出失败,失败原因：" + e.getMessage());
        }
    }

    /**
     * 全部导出
     *
     * @param response 响应
     */
    @Override
    public void exportAll(HttpServletResponse response) {
        try {
            String fileName = "loginLog" + DateUtils.getYearMonthDay(new Date()) + ".xlsx";
            String sheetName = "登录日志";
            ExcelUtils.export(response, this.queryLoginLogListAll(), SysLoginLogVo.class, fileName, sheetName);
        } catch (Exception e) {
            throw new ServiceException("日志导出失败,失败原因：" + e.getMessage());
        }
    }

    /**
     * 查询登录日志列表
     *
     * @param ids 身份证件
     * @return {@link List}<{@link SysLoginLogVo}>
     */
    private List<SysLoginLogVo> queryLoginLogList(List<Long> ids) {
        LambdaQueryWrapper<SysLoginLog> loginLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        loginLogLambdaQueryWrapper.in(SysLoginLog::getId, ids);
        List<SysLoginLogVo> sysLoginLogVoList = baseMapper.queryVoList(loginLogLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(sysLoginLogVoList)) {
            return Collections.emptyList();
        }
        List<Long> userIdList = sysLoginLogVoList.stream().map(SysLoginLogVo::getUserId).collect(Collectors.toList());
        List<SysUser> userList = sysUserService.listByIds(userIdList);
        for (SysLoginLogVo sysLoginLogVo : sysLoginLogVoList) {
            Long userId = sysLoginLogVo.getUserId();
           SysUser user = userList.stream().filter(v -> v.getId().equals(userId)).findFirst().orElse(null);
            if (ObjectUtils.isNotNull(user)) {
                sysLoginLogVo.setUsername(user.getUsername());
                sysLoginLogVo.setUserNickName(user.getNickName());
            }
        }
        return sysLoginLogVoList;
    }

    /**
     * 获取全部登录日志列表
     *
     * @return {@link List}<{@link SysLoginLogVo}>
     */
    private List<SysLoginLogVo> queryLoginLogListAll() {
        List<SysLoginLogVo> loginLogBackVoList = baseMapper.queryVoList(new LambdaQueryWrapper<>());
        if (CollectionUtils.isEmpty(loginLogBackVoList)) {
            return Collections.emptyList();
        }
        List<Long> userIdList = loginLogBackVoList.stream().map(SysLoginLogVo::getUserId).collect(Collectors.toList());
        List<SysUser> userList = sysUserService.listByIds(userIdList);
        for (SysLoginLogVo sysLoginLogVo : loginLogBackVoList) {
            Long userId = sysLoginLogVo.getUserId();
            SysUser user = userList.stream().filter(v -> v.getId().equals(userId)).findFirst().orElse(null);
            if (ObjectUtils.isNotNull(user)) {
                sysLoginLogVo.setUsername(user.getUsername());
                sysLoginLogVo.setUserNickName(user.getNickName());
            }
        }
        return loginLogBackVoList;
    }

    /**
     * 列出登录日志
     *
     * @param sysLoginLogDto 系统登录日志
     * @param queryParams     查询参数
     * @return {@link PagingData}<{@link SysLoginLogVo}>
     */
    @Override
    public PagingData<SysLoginLogVo> listLoginLog(SysLoginLogDto sysLoginLogDto, QueryParams queryParams) {
        LambdaQueryWrapper<SysLoginLog> loginLogLambdaQueryWrapper = this.buildLambdaQueryWrapper(sysLoginLogDto);
        loginLogLambdaQueryWrapper.orderByDesc(SysLoginLog::getCreateTime);
        Page<SysLoginLogVo> sysLoginLogVoPage = baseMapper.queryVoPage(queryParams.build(), loginLogLambdaQueryWrapper);
        PagingData<SysLoginLogVo> pagingData = PagingData.build(sysLoginLogVoPage);
        List<SysLoginLogVo> sysLoginLogVoList = pagingData.getDataList();
        for (SysLoginLogVo sysLoginLogVo : sysLoginLogVoList) {
            Long userId = sysLoginLogVo.getUserId();
            SysUser user = sysUserService.getById(userId);
            sysLoginLogVo.setUsername(user.getUsername());
            sysLoginLogVo.setUserNickName(user.getNickName());
            IpHome ipHome = IpUtils.completeIpHome(sysLoginLogVo.getLoginIp());
            sysLoginLogVo.setLoginIpHome(ipHome);
        }
        return pagingData;
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysLoginLogDto 系统登录日志
     * @return {@link LambdaQueryWrapper}<{@link SysLoginLog}>
     */
    private LambdaQueryWrapper<SysLoginLog> buildLambdaQueryWrapper(SysLoginLogDto sysLoginLogDto) {
        LambdaQueryWrapper<SysLoginLog> loginLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysLoginLogDto)) {
            loginLogLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysLoginLogDto.getId()), SysLoginLog::getId, sysLoginLogDto.getId());
            loginLogLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysLoginLogDto.getLoginType()), SysLoginLog::getLoginType, sysLoginLogDto.getLoginType());
            loginLogLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysLoginLogDto.getLoginPlatform()), SysLoginLog::getLoginPlatform, sysLoginLogDto.getLoginPlatform());
            loginLogLambdaQueryWrapper.like(StringUtils.isNotBlank(sysLoginLogDto.getBrowserName()), SysLoginLog::getBrowserName, sysLoginLogDto.getBrowserName());
            loginLogLambdaQueryWrapper.like(StringUtils.isNotBlank(sysLoginLogDto.getOsName()), SysLoginLog::getOsName, sysLoginLogDto.getOsName());
        }
        return loginLogLambdaQueryWrapper;
    }

}
