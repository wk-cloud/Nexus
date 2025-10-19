package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.helper.LoginHelper;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.TokenUtils;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.domain.dto.SysOnlineUserDto;
import com.nexus.system.domain.dto.SysUserDto;
import com.nexus.system.domain.vo.SysOnlineUserVo;
import com.nexus.system.domain.vo.SysUserVo;
import com.nexus.system.mapper.SysOnlineUserMapper;
import com.nexus.system.service.SysOnlineUserService;
import com.nexus.system.service.SysRoleService;
import com.nexus.system.service.SysUserRoleService;
import com.nexus.system.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户服务实现
 *
 * @author wk
 * @date 2024/07/06
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class SysOnlineUserServiceImpl extends ServiceImpl<SysOnlineUserMapper, SysOnlineUser> implements SysOnlineUserService {

    @Resource
    private SysOnlineUserMapper baseMapper;
    @Resource
    @Lazy
    private SysUserService sysUserService;

    /**
     * 保存在线用户
     *
     * @param onlineUser 在线用户
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveOnlineUser(SysOnlineUser onlineUser) {
        LambdaQueryWrapper<SysOnlineUser> onlineUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        onlineUserLambdaQueryWrapper
                .eq(SysOnlineUser::getUserId, onlineUser.getUserId())
                .eq(SysOnlineUser::getLoginPlatform, onlineUser.getLoginPlatform());
        baseMapper.delete(onlineUserLambdaQueryWrapper);
        return super.save(onlineUser);
    }

    /**
     * 离线
     *
     * @param userId        用户 ID
     * @param loginPlatform 登录平台
     * @return {@link Boolean}
     */
    @Override
    public Boolean offline(Long userId, Integer loginPlatform) {
        LambdaQueryWrapper<SysOnlineUser> onlineUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        onlineUserLambdaQueryWrapper.eq(SysOnlineUser::getUserId, userId).eq(SysOnlineUser::getLoginPlatform, loginPlatform);
        SysOnlineUser onlineUser = baseMapper.selectOne(onlineUserLambdaQueryWrapper);
        String loginToken = onlineUser.getLoginToken();
        TokenUtils.removeTokenFromRedisSet(loginToken);
        LoginHelper.removeLoginUserByToken(loginToken);
        return super.remove(onlineUserLambdaQueryWrapper);
    }

    /**
     * 查询在线用户列表
     *
     * @param onlineUserBackDto 在线用户 DTO
     * @param queryParams       查询参数
     * @return {@link PagingData}<{@link SysOnlineUserVo}>
     */
    public PagingData<SysOnlineUserVo> listOnlineUser(SysOnlineUserDto onlineUserBackDto, QueryParams queryParams) {
        SysUserDto sysUserDto = BeanUtils.toBean(onlineUserBackDto, SysUserDto.class);
        PagingData<SysUserVo> sysUserVoPagingData = sysUserService.listUser(sysUserDto, queryParams);
        List<SysUserVo> sysUserVoList = sysUserVoPagingData.getDataList();
        if (CollectionUtils.isEmpty(sysUserVoList)) {
            return PagingData.build(Collections.emptyList(), 0L, false, false);
        }
        List<Long> userIdList = sysUserVoList.stream().map(SysUserVo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<SysOnlineUser> onlineUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        onlineUserLambdaQueryWrapper.in(SysOnlineUser::getUserId, userIdList);
        Page<SysOnlineUserVo> sysOnlineUserVoPage = baseMapper.queryVoPage(queryParams.build(), onlineUserLambdaQueryWrapper);
        PagingData<SysOnlineUserVo> pagingData = PagingData.build(sysOnlineUserVoPage);
        List<SysOnlineUserVo> sysOnlineUserVoList = pagingData.getDataList();
        for (SysOnlineUserVo sysOnlineUserVo : sysOnlineUserVoList) {
            SysUserVo user = sysUserVoList.stream()
                    .filter(userBackVo -> userBackVo.getId().equals(sysOnlineUserVo.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (ObjectUtils.isNotNull(user)) {
                BeanUtils.copyProperties(user, sysOnlineUserVo, "loginTime");
            }
        }
        return pagingData;
    }

    /**
     * 删除离线用户
     *
     * @param token 令 牌
     * @return {@link Boolean}
     */
    @Override
    public Boolean removeOfflineUser(String token) {
        LambdaQueryWrapper<SysOnlineUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysOnlineUser::getLoginToken, token);
        return super.remove(lambdaQueryWrapper);
    }
}
