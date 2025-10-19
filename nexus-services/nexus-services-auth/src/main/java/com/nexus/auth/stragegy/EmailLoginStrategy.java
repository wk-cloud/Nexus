package com.nexus.auth.stragegy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.core.base.LoginUser;
import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.helper.LoginHelper;
import com.nexus.common.enums.LoginTypeEnum;
import com.nexus.common.enums.VerificationCodeTypeEnum;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.*;
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
 * 邮箱登录
 * @author wk
 * @date 2025/04/05
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class EmailLoginStrategy extends AbstractLoginStrategy {
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 获取在子类中声明的登录类型
     *
     * @return {@link Integer}
     */
    @Override
    protected Integer getLoginType() {
        return LoginTypeEnum.EMAIL.getCode();
    }

    /**
     * 登录处理器
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo }
     */
    @Override
    protected LoginVo loginProcessor(LoginDto loginDto) {
        HttpServletRequest request = LoginHelper.getRequest();
        SysUser user;
        if(this.isPasswordLogin(loginDto)){
            user = passwordLogin(loginDto);
        }else if(this.isPasswordFreeLogin(loginDto)){
            user = passwordFreeLogin(loginDto);
        }else {
            user = defaultLogin(loginDto);
        }
        // 更新用户登录ip，登录时间、登录方式
        String ipAddress = IpUtils.getIpAddress(request);
        Integer loginType = VerificationUtils.getLoginType(user.getEmail());
        user.setLoginIp(ipAddress);
        user.setLoginTime(LocalDateTime.now());
        user.setLoginType(loginType);
        sysUserMapper.updateById(user);
        // 生成token
        HashMap<String, String> payLoad = new HashMap<>(CollectionUtils.getInitialCapacity(3));
        payLoad.put("userId", Long.toString(user.getId()));
        String token = TokenUtils.createTokenForRedisSet(payLoad);
        // 暂存用户信息
        LoginUser loginUser = BeanUtils.toBean(user, LoginUser.class);
        loginUser.setToken(token);
        loginUser.setUserId(user.getId());
        LoginHelper.setLoginUser(loginUser);
        // 保存在线信息
        SysOnlineUser onlineUser = new SysOnlineUser();
        onlineUser.setUserId(user.getId());
        onlineUser.setLoginPlatform(loginDto.getLoginPlatform());
        onlineUser.setLoginToken(token);
        onlineUser.setLoginTime(LocalDateTime.now());
        super.saveOnlineUser(onlineUser);
        // 返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setLoginFlag(true);
        loginVo.setLoginType(LoginTypeEnum.EMAIL.getCode());
        return loginVo;
    }

    /**
     * 是免密码登录
     *
     * @param loginDto 登录信息
     * @return boolean
     */
    private boolean isPasswordFreeLogin(LoginDto loginDto){
        return StringUtils.isNotBlank(loginDto.getVerificationCode()) && StringUtils.isBlank(loginDto.getPassword());
    }

    /**
     * 是密码登录
     *
     * @param loginDto 登录信息
     * @return boolean
     */
    private boolean isPasswordLogin(LoginDto loginDto){
        return StringUtils.isNotBlank(loginDto.getPassword()) && StringUtils.isBlank(loginDto.getVerificationCode());
    }

    /**
     * 是默认登录
     *
     * @param loginDto 登录信息
     * @return boolean
     */
    private boolean isDefaultLogin(LoginDto loginDto){
        return !isPasswordLogin(loginDto) && !isPasswordFreeLogin(loginDto);
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
        SysUser user = sysUserMapper.selectOne(userLambdaQueryWrapper);
        if (ObjectUtils.isNull(user)) {
            throw new ServiceException("账号不存在，请先注册");
        }
        // 检查用户是否被禁止登录
        if (user.getDisabled()) {
            throw new ServiceException("登录失败，当前账号已被禁止登录，请联系管理员进行账号解封");
        }
        String salt = user.getSalt();
        password = EncryptionUtils.passwordEncryption(password, salt);
        if (!user.getPassword().equals(password)) {
            throw new ServiceException("密码错误");
        }
        return user;
    }

    /**
     * 默认登录
     *
     * @param loginDto 登录信息
     * @return {@link SysUser }
     */
    private SysUser defaultLogin(LoginDto loginDto) {
        // 校验验证码
        String verificationCodeKey = VerificationCodeTypeEnum.getKey(loginDto.getVerificationCodeType()) + loginDto.getEmail().trim();
        String verificationCode = (String) RedisUtils.get(verificationCodeKey);
        if (StringUtils.isBlank(verificationCode) || !loginDto.getVerificationCode().equalsIgnoreCase(verificationCode)) {
            throw new ServiceException("验证码错误");
        }
        // 校验用户密码和邮箱并返回用户信息
        SysUser user = this.verificationEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());
        // 验证成功后，清除验证码
        RedisUtils.delete(verificationCodeKey);
        return user;
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
     * 免密码登录
     *
     * @param loginDto 登录信息
     * @return {@link SysUser }
     */
    private SysUser passwordFreeLogin(LoginDto loginDto) {
        // 校验验证码
        String verificationCodeKey = VerificationCodeTypeEnum.getKey(loginDto.getVerificationCodeType()) + loginDto.getEmail().trim();
        String verificationCode = (String) RedisUtils.get(verificationCodeKey);
        if (StringUtils.isBlank(verificationCode) || !loginDto.getVerificationCode().equalsIgnoreCase(verificationCode)) {
            throw new ServiceException("验证码错误");
        }
        // 验证成功后，清除验证码
        RedisUtils.delete(verificationCodeKey);
        // 检查用户是否存在
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(SysUser::getEmail, loginDto.getEmail()).last("limit 1");
        SysUser user = sysUserMapper.selectOne(userLambdaQueryWrapper);
        if (ObjectUtils.isNull(user)) {
            throw new ServiceException("登录失败，账号不存在");
        }
        // 检查用户是否被禁止登录
        if (user.getDisabled()) {
            throw new ServiceException("登录失败，当前账号已被禁止登录，请联系管理员进行账号解封");
        }
        return user;
    }


}
