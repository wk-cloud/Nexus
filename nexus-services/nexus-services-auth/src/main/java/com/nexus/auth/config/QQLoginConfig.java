package com.nexus.auth.config;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.nexus.auth.config.properties.QqLoginProperties;
import com.nexus.common.core.utils.CollectionUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * qq登录配置
 *
 * @author wk
 * @date 2023/2/19 14:06
 */
@Configuration
public class QQLoginConfig {

    /**
     * 获取 access_token 的URL
     */
    private static final String ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

    /**
     * 获取 openid 的URL
     */
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me";

    /**
     * 获取用户信息的URL
     */
    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info";

    @Resource
    private QqLoginProperties qqLoginProperties;

    /**
     * 获取用户信息
     *
     * @param code 代码
     * @return {@link Map}<{@link String},{@link Object}>
     */
    public Map<String,Object> getUserInfo(String code){
        HashMap<String, Object> paramMap = new HashMap<>(CollectionUtils.initialCapacity(6));
        paramMap.put("client_id", qqLoginProperties.getClientId());
        paramMap.put("client_secret", qqLoginProperties.getClientSecret());
        paramMap.put("grant_type", qqLoginProperties.getGrantType());
        paramMap.put("redirect_uri", qqLoginProperties.getRedirectUri());
        paramMap.put("fmt", qqLoginProperties.getFmt());
        paramMap.put("code", code);
        // 1. 获取 access_token
        String accessSource = HttpUtil.get(ACCESS_TOKEN_URL, paramMap);
        Map<String,Object> accessTokenMap = JSONUtil.parseObj(accessSource);
        Object accessToken = accessTokenMap.get("access_token");

        // 2. 获取 openid
        paramMap.clear();
        paramMap.put("access_token", accessToken);
        paramMap.put("fmt", "json");
        String openIdSource = HttpUtil.get(OPEN_ID_URL, paramMap);
        Map<String,Object> openIdMap = JSONUtil.parseObj(openIdSource);
        String openid = (String) openIdMap.get("openid");
        // 3. 获取用户信息
        paramMap.clear();
        paramMap.put("access_token", accessToken);
        paramMap.put("oauth_consumer_key", qqLoginProperties.getClientId());
        paramMap.put("openid", openid);
        String userInfoSource = HttpUtil.get(USER_INFO_URL, paramMap);
        Map<String,Object> userInfoMap = (Map<String, Object>) JSON.parse(userInfoSource);
        userInfoMap.put("openId",openid);
        return userInfoMap;
    }

}
