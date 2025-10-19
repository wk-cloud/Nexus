package com.nexus.auth.stragegy;

import com.nexus.common.core.base.LoginUser;
import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.helper.LoginHelper;
import com.nexus.common.enums.LoginTypeEnum;
import com.nexus.common.enums.RoleEnum;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.*;
import com.nexus.framework.config.QQLoginConfig;
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
        SysUser user = this.qqLogin(loginDto, LoginHelper.getRequest());
        // 生成token
        HashMap<String, String> payLoad = new HashMap<>(CollectionUtils.getInitialCapacity(2));
        payLoad.put("userId", Long.toString(user.getId()));
        String token = TokenUtils.createTokenForRedisSet(payLoad);
        // 保存在线信息
        SysOnlineUser onlineUser = new SysOnlineUser();
        onlineUser.setUserId(user.getId());
        onlineUser.setLoginPlatform(loginDto.getLoginPlatform());
        onlineUser.setLoginToken(token);
        onlineUser.setLoginTime(LocalDateTime.now());
        super.saveOnlineUser(onlineUser);
        // 暂存用户信息
        LoginUser loginUser = BeanUtils.toBean(user, LoginUser.class);
        loginUser.setToken(token);
        loginUser.setUserId(user.getId());
        LoginHelper.setLoginUser(loginUser);
        // 返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setLoginFlag(true);
        loginVo.setLoginType(LoginTypeEnum.QQ.getCode());
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
        SysUser user = super.queryUserInfoByOpenId(openId);
        if (ObjectUtils.isNull(user)) {
            user = new SysUser();
            // 如果成功获取到用户信息，则允许登录并保存信息
            // 1. 登录ip
            String ipAddress = IpUtils.getIpAddress(request);
            user.setLoginIp(ipAddress);
            // 2. 登录时间
            user.setLoginTime(LocalDateTime.now());
            // 3. 登录方式
            Integer loginType = LoginTypeEnum.QQ.getCode();
            user.setLoginType(loginType);
            // 4. 生成用户名
            String userName = UserNameUtils.generate(loginType);
            user.setUsername(userName);
            // 5. openId
            user.setOpenid((String) userInfoMap.get("openId"));
            // 6. 昵称
            user.setNickName((String) userInfoMap.get("nickname"));
            // 7. 头像
            user.setAvatar((String) userInfoMap.get("figureurl_qq"));
            sysUserMapper.insert(user);
            // 设置用户角色信息为user
            Long roleId = super.queryRoleIdByRoleLabel(RoleEnum.USER.getRoleLabel());
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        } else {
            if (user.getDisabled()) {
                throw new ServiceException("登录失败，当前账号已被禁止登录，请联系管理员进行账号解封");
            }
            // 1. 登录ip
            String ipAddress = IpUtils.getIpAddress(request);
            user.setLoginIp(ipAddress);
            // 2. 登录时间
            user.setLoginTime(LocalDateTime.now());
            // 3. 登录方式
            Integer loginType = LoginTypeEnum.QQ.getCode();
            user.setLoginType(loginType);
            // 4. 昵称
            user.setNickName((String) userInfoMap.get("nickname"));
            // 5. 头像
            user.setAvatar((String) userInfoMap.get("figureurl_qq"));
            sysUserMapper.updateById(user);
        }
        return user;
    }
}
