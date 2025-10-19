package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存 key 枚举
 *
 * @author wk
 * @date 2024/08/24
 */
@AllArgsConstructor
@Getter
public enum CacheKeyEnum {

    FRONT_QUERY_EMOJI_LIST("front:query:emoji:list"),
    FRONT_QUERY_BANNER_LIST("front:query:banner:list"),
    FRONT_QUERY_WEBSITE_INFO("front:query:website:info"),
    FRONT_QUERY_WEBSITE_CONFIG("front:query:website:config"),
    FRONT_QUERY_BARRAGE_CONFIG("front:query:barrage:config");

    private final String key;

}
