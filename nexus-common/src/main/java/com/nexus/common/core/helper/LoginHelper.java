package com.nexus.common.core.helper;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson2.JSON;
import com.nexus.common.core.base.LoginUser;
import com.nexus.common.enums.AdminEnum;
import com.nexus.common.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 登录助手
 *
 * @author wk
 * @date 2025/03/27
 */
@Slf4j
public class LoginHelper {

    /**
     * 登录助手key
     */
    private static final String LOGIN_HELPER_KEY = "login:helper:hash";

    /**
     * 登录用户映射
     */
    private static final Map<String, LoginUser> loginUserMap = new ConcurrentHashMap<>();

    /**
     * 锁
     */
    private static final ReentrantLock LOCK = new ReentrantLock();

    private LoginHelper() {
    }

    /**
     * 判断是否登录
     *
     * @return boolean
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }

    /**
     * 判断是否是超级管理员
     *
     * @return boolean
     */
    public static boolean isSuperAdmin() {
        if (!isLogin()) {
            return false;
        }
        return AdminEnum.SUPER_ADMIN.getEmail().equals(getLoginUser().getEmail());
    }

    /**
     * 获取登录用户 ID
     *
     * @return {@link Long}
     */
    public static Long getUserId() {
        return isLogin() ? getLoginUser().getId() : null;
    }

    /**
     * 获取登录用户邮箱
     *
     * @return {@link String}
     */
    public static String getEmail() {
        return isLogin() ? getLoginUser().getEmail() : null;
    }

    /**
     * 获取登录用户名
     *
     * @return {@link String}
     */
    public static String getUsername() {
        return isLogin() ? getLoginUser().getUsername() : null;
    }

    /**
     * 获取登录用户昵称
     *
     * @return {@link String}
     */
    public static String getNickName() {
        return isLogin() ? getLoginUser().getNickName() : null;
    }

    /**
     * 获取登录用户令牌
     *
     * @return {@link String}
     */
    public static String getToken() {
        return isLogin() ? getLoginUser().getToken() : null;
    }

    /**
     * 从请求头获取令牌
     *
     * @return {@link String}
     */
    public static String getTokenFromHeader() {
        return getRequest().getHeader("Authorization");
    }

    /**
     * 获取请求
     *
     * @return {@link HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        return SpringMvcUtils.getRequest();
    }

    /**
     * 获取响应
     *
     * @return {@link HttpServletResponse}
     */
    public static HttpServletResponse getResponse() {
        return SpringMvcUtils.getResponse();
    }

    /**
     * 获取会话
     *
     * @return {@link HttpSession}
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取会话 ID
     *
     * @return {@link String}
     */
    public static String getSessionId() {
        return getSession().getId();
    }

    /**
     * 获取登录用户
     *
     * @return {@link LoginUser}
     */
    public static LoginUser getLoginUser() {
        return getLoginUserByToken(getTokenFromHeader());
    }

    /**
     * 获取登录用户
     *
     * @return {@link LoginUser}
     */
    public static LoginUser getLoginUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            log.error("====> 当前会话未登录，请重新登录");
            return null;
        }
        LOCK.lock();
        try {
            // 1. 先从内存中获取
            LoginUser loginUser = LoginHelper.loginUserMap.get(token);
            if (loginUser == null) {
                // 2. 内存中获取失败，尝试从 redis 中获取
                try {
                    String data = (String) RedisUtils.hGet(LOGIN_HELPER_KEY, token);
                    if (StringUtils.isBlank(data)) {
                        log.error("====> 当前会话未登录，请重新登录");
                    } else {
                        loginUser = JSON.parseObject(data, LoginUser.class);
                        LoginHelper.loginUserMap.put(token, loginUser);
                    }
                } catch (Exception e) {
                    log.error("====> 当前会话未登录，请重新登录");
                }
            }
            return loginUser;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 设置登录用户
     *
     * @param loginUser 登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        LoginHelper.loginUserMap.put(loginUser.getToken(), loginUser);
        RedisUtils.hPut(LOGIN_HELPER_KEY, loginUser.getToken(), JSONUtils.toJSONString(loginUser));
    }

    /**
     * 删除登录用户
     */
    public static void removeLoginUser() {
        removeLoginUserByToken(getTokenFromHeader());
    }

    /**
     * 删除登录用户
     */
    public static void removeLoginUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            log.error("====> 当前会话未登录，请重新登录");
            return;
        }
        LoginHelper.loginUserMap.remove(token);
        RedisUtils.hDelete(LOGIN_HELPER_KEY, token);
    }

    /**
     * 删除过期登录用户
     */
    public static void removeExpiredLoginUser(){
        Set<String> tokens = RedisUtils.hKeys(LOGIN_HELPER_KEY);
        if(CollectionUtils.isNotEmpty(tokens)){
            tokens.forEach(token -> {
                if(TokenUtils.isExpired(token)){
                    removeLoginUserByToken(token);
                }
            });
        }
    }
}
