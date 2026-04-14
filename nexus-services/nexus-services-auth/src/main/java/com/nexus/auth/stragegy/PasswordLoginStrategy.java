package com.nexus.auth.stragegy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.enums.LoginTypeEnum;
import com.nexus.common.core.exception.ServiceException;
import com.nexus.common.core.utils.*;
import com.nexus.common.shiro.domain.LoginDto;
import com.nexus.common.shiro.domain.LoginUser;
import com.nexus.common.shiro.helper.LoginHelper;
import com.nexus.common.token.utils.TokenUtils;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.domain.SysUser;
import com.nexus.system.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * 密码登录策略
 *
 * @author wk
 * @date 2026/4/13 18:13
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class PasswordLoginStrategy extends AbstractLoginStrategy {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    protected Integer getLoginType() {
        return LoginTypeEnum.PASSWORD.getCode();
    }

    @Override
    protected LoginVo loginProcessor(LoginDto loginDto) {
        HttpServletRequest request = LoginHelper.getRequest();
        SysUser sysUser = this.passwordLogin(loginDto);
        // 更新用户登录ip，登录时间、登录方式
        String ipAddress = IpUtils.getIpAddress(request);
        sysUser.setLoginIp(ipAddress);
        sysUser.setLoginTime(LocalDateTime.now());
        sysUser.setLoginType(getLoginType());
        sysUserMapper.updateById(sysUser);
        // 生成token
        HashMap<String, String> payLoad = new HashMap<>(CollectionUtils.initialCapacity(3));
        payLoad.put("userId", Long.toString(sysUser.getId()));
        String token = TokenUtils.createTokenForRedisSet(payLoad);
        // 暂存用户信息
        LoginUser loginUser = BeanUtils.toBean(sysUser, LoginUser.class);
        loginUser.setToken(token);
        loginUser.setUserId(sysUser.getId());
        LoginHelper.setLoginUser(loginUser);
        // 保存在线信息
        SysOnlineUser onlineUser = new SysOnlineUser();
        onlineUser.setUserId(sysUser.getId());
        onlineUser.setLoginPlatform(loginDto.getLoginPlatform());
        onlineUser.setLoginToken(token);
        onlineUser.setLoginTime(LocalDateTime.now());
        onlineUser.setLoginType(getLoginType());
        super.saveOnlineUser(onlineUser);
        // 返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setLoginFlag(true);
        loginVo.setLoginType(getLoginType());
        return loginVo;
    }

    /**
     * 密码登录
     *
     * @param loginDto 登录信息
     * @return {@link SysUser }
     */
    private SysUser passwordLogin(LoginDto loginDto) {
        // 检查用户邮箱和密码
        return this.verificationEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());
    }

    /**
     * 验证电子邮件和密码
     *
     * @param email    电子邮件
     * @param password 密码
     * @return {@link SysUser }
     */
    private SysUser verificationEmailAndPassword(String email, String password) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(SysUser::getEmail, email).last("limit 1");
        SysUser sysUser = sysUserMapper.selectOne(userLambdaQueryWrapper);
        if (ObjectUtils.isNull(sysUser)) {
            throw new ServiceException("账号或密码无效");
        }
        String salt = sysUser.getSalt();
        password = EncryptionUtils.passwordEncryption(password, salt);
        if (!sysUser.getPassword().equals(password)) {
            throw new ServiceException("账号或密码无效");
        }
        // 检查用户是否被禁止登录
        if (sysUser.getDisabled()) {
            throw new ServiceException("登录失败，当前账号已被禁止登录，请联系管理员进行账号解封");
        }
        return sysUser;
    }
}
