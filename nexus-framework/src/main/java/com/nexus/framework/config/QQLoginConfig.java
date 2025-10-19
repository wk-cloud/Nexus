package com.nexus.framework.config;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.nexus.common.utils.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName QQLoginConfig
 * @Description TODO
 * @Author wk
 * @Date 2023/2/19 14:06
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "qq")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QQLoginConfig {

    private String clientId;
    private String clientSecret;
    private String grantType;
    private String redirectUri;
    private String fmt;

    /**
     * 获取用户信息
     *
     * @param code 代码
     * @return {@link Map}<{@link String},{@link Object}>
     */
    public Map<String,Object> getUserInfoMap(String code){
        HashMap<String, Object> paramMap = new HashMap<>(CollectionUtils.getInitialCapacity(6));
        paramMap.put("client_id", clientId);
        paramMap.put("client_secret", clientSecret);
        paramMap.put("grant_type", grantType);
        paramMap.put("redirect_uri", redirectUri);
        paramMap.put("fmt", fmt);
        paramMap.put("code", code);
        // 1. 获取 access_token
        String accessSource = HttpUtil.get("https://graph.qq.com/oauth2.0/token", paramMap);
        Map<String,Object> accessTokenMap = JSONUtil.parseObj(accessSource);
        Object accessToken = accessTokenMap.get("access_token");

        // 2. 获取 openid
        paramMap.clear();
        paramMap.put("access_token", accessToken);
        paramMap.put("fmt", "json");
        String openIdSource = HttpUtil.get("https://graph.qq.com/oauth2.0/me", paramMap);
        Map<String,Object> openIdMap = JSONUtil.parseObj(openIdSource);
        String openid = (String) openIdMap.get("openid");
        // 3. 获取用户信息
        paramMap.clear();
        paramMap.put("access_token", accessToken);
        paramMap.put("oauth_consumer_key", clientId);
        paramMap.put("openid", openid);
        String userInfoSource = HttpUtil.get("https://graph.qq.com/user/get_user_info", paramMap);
        Map<String,Object> userInfoMap = (Map<String, Object>) JSON.parse(userInfoSource);
        userInfoMap.put("openId",openid);
        return userInfoMap;
    }

}
