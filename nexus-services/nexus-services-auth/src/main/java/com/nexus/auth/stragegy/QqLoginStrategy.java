package com.nexus.auth.stragegy;

import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.enums.LoginTypeEnum;
import com.nexus.common.core.enums.RoleEnum;
import com.nexus.common.core.exception.ServiceException;
import com.nexus.common.core.utils.*;
import com.nexus.common.shiro.domain.LoginDto;
import com.nexus.common.shiro.domain.LoginUser;
import com.nexus.common.shiro.helper.LoginHelper;
import com.nexus.common.token.utils.TokenUtils;
import com.nexus.auth.config.QQLoginConfig;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.mapper.SysUserMapper;
import com.nexus.system.mapper.SysUserRoleMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * qq登录
 * @author wk
 * @date 2025/04/05
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class QqLoginStrategy extends AbstractLoginStrategy{

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private QQLoginConfig qqLoginConfig;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 获取登录类型
     *
     * @return {@link Integer }
     */
    @Override
    protected Integer getLoginType() {
        return LoginTypeEnum.QQ.getCode();
    }

    /**
     * 登录处理器
     *
     * @param loginDto 登录信息
     * @return {@link LoginVo }
     */
    @Override
    protected LoginVo loginProcessor(LoginDto loginDto) {
        SysUser sysUser = this.qqLogin(loginDto, LoginHelper.getRequest());
        // 生成token
        HashMap<String, String> payLoad = new HashMap<>(CollectionUtils.initialCapacity(2));
        payLoad.put("userId", Long.toString(sysUser.getId()));
        String token = TokenUtils.createTokenForRedisSet(payLoad);
        // 保存在线信息
        SysOnlineUser onlineUser = new SysOnlineUser();
        onlineUser.setUserId(sysUser.getId());
        onlineUser.setLoginPlatform(loginDto.getLoginPlatform());
        onlineUser.setLoginToken(token);
        onlineUser.setLoginTime(LocalDateTime.now());
        onlineUser.setLoginType(getLoginType());
        super.saveOnlineUser(onlineUser);
        // 暂存用户信息
        LoginUser loginUser = BeanUtils.toBean(sysUser, LoginUser.class);
        loginUser.setToken(token);
        loginUser.setUserId(sysUser.getId());
        LoginHelper.setLoginUser(loginUser);
        // 返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setLoginFlag(true);
        loginVo.setLoginType(getLoginType());
        return loginVo;
    }

    /**
     * qq登录
     *
     * @param loginDto 登录信息
     * @param request 请求
     * @return {@link SysUser }
     */
    private SysUser qqLogin(LoginDto loginDto, HttpServletRequest request) {
        // 登录凭证
        String code = loginDto.getCode();
        // 获取qq用户信息
        Map<String, Object> userInfoMap = qqLoginConfig.getUserInfoMap(code);
        if (CollectionUtils.isEmpty(userInfoMap)) {
            throw new ServiceException("登录失败，QQ账户信息获取失败");
        }
        String openId = (String) userInfoMap.get("openId");
        if (StringUtils.isBlank(openId)) {
            throw new ServiceException("登录失败，QQ账户信息获取失败");
        }
        SysUser sysUser = super.queryUserInfoByOpenId(openId);
        if (ObjectUtils.isNull(sysUser)) {
            sysUser = new SysUser();
            // 如果成功获取到用户信息，则允许登录并保存信息
            // 1. 登录ip
            String ipAddress = IpUtils.getIpAddress(request);
            sysUser.setLoginIp(ipAddress);
            // 2. 登录时间
            sysUser.setLoginTime(LocalDateTime.now());
            // 3. 登录方式
            Integer loginType = getLoginType();
            sysUser.setLoginType(loginType);
            // 4. 生成用户名
            String userName = UserNameUtils.generate(loginType, 10);
            sysUser.setUsername(userName);
            // 5. openId
            sysUser.setOpenid((String) userInfoMap.get("openId"));
            // 6. 昵称
            sysUser.setNickName((String) userInfoMap.get("nickname"));
            // 7. 头像
            sysUser.setAvatar((String) userInfoMap.get("figureurl_qq"));
            sysUserMapper.insert(sysUser);
            // 设置用户角色信息为user
            Long roleId = super.queryRoleIdByRoleLabel(RoleEnum.USER.getRoleLabel());
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(sysUser.getId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleMapper.insert(sysUserRole);
        } else {
            if (sysUser.getDisabled()) {
                throw new ServiceException("登录失败，当前账号已被禁止登录，请联系管理员进行账号解封");
            }
            // 1. 登录ip
            String ipAddress = IpUtils.getIpAddress(request);
            sysUser.setLoginIp(ipAddress);
            // 2. 登录时间
            sysUser.setLoginTime(LocalDateTime.now());
            // 3. 登录方式
            sysUser.setLoginType(getLoginType());
            // 4. 昵称
            sysUser.setNickName((String) userInfoMap.get("nickname"));
            // 5. 头像
            sysUser.setAvatar((String) userInfoMap.get("figureurl_qq"));
            sysUserMapper.updateById(sysUser);
        }
        return sysUser;
    }
}
