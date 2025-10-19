package com.nexus.auth.stragegy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.SysUser;
import com.nexus.system.mapper.SysOnlineUserMapper;
import com.nexus.system.mapper.SysRoleMapper;
import com.nexus.system.mapper.SysUserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 登录策略抽象层
 * @author wk
 * @date 2025/04/05
 */
@Component
public abstract class AbstractLoginStrategy implements LoginStrategy {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysOnlineUserMapper sysOnlineUserMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    /**
     * 登录策略集合
     */
    public static ConcurrentMap<Integer, AbstractLoginStrategy> loginStrategyMap = new ConcurrentHashMap<>();

    /**
     * 初始化登录策略
     *
     * */
    @PostConstruct
    private void init() {
        loginStrategyMap.put(getLoginType(), this);
    }

    /**
     * 保存在线用户
     *
     * @param sysOnlineUser 在线用户
     * @return {@link Boolean }
     */
    protected Boolean saveOnlineUser(SysOnlineUser sysOnlineUser) {
        LambdaQueryWrapper<SysOnlineUser> onlineUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        onlineUserLambdaQueryWrapper
                .eq(SysOnlineUser::getUserId,sysOnlineUser.getUserId())
                .eq(SysOnlineUser::getLoginPlatform,sysOnlineUser.getLoginPlatform());
        sysOnlineUserMapper.delete(onlineUserLambdaQueryWrapper);
        return sysOnlineUserMapper.insert(sysOnlineUser) > 0;
    }

    /**
     * 按open id查询用户信息
     *
     * @param openid openid
     * @return {@link SysUser }
     */
    protected SysUser queryUserInfoByOpenId(String openid) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(SysUser::getOpenid, openid).last("limit 1");
        return sysUserMapper.selectOne(userLambdaQueryWrapper);
    }

    /**
     * 按角色标签查询角色id
     *
     * @param roleLabel 角色标签
     * @return {@link Long }
     */
    protected Long queryRoleIdByRoleLabel(String roleLabel) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.select(SysRole::getId).eq(SysRole::getLabel, roleLabel).last("limit 1");
        SysRole role = sysRoleMapper.selectOne(roleLambdaQueryWrapper);
        return ObjectUtils.isNotNull(role) ? role.getId() : null;
    }


    /**
     * 获取在子类中声明的登录类型
     *
     * @return {@link Integer}
     */
    protected abstract Integer getLoginType();

    /**
     * 登录
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo }
     */
    @Override
    public LoginVo login(LoginDto loginDto) {
        return loginProcessor(loginDto);
    }

    /**
     * 登录执行器
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo}
     */
    protected abstract LoginVo loginProcessor(LoginDto loginDto);
}
