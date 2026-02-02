package com.nexus.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聊天内容类型枚举
 *
 * @author wk
 * @date 2025/12/31
 */
@Getter
@AllArgsConstructor
public enum MessageContentTypeEnum {

    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    VIDEO(3, "视频"),
    AUDIO(4, "音频"),
    FILE(5, "文件");

    private final Integer code;
    private final String info;
}
