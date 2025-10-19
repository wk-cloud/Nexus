package com.nexus.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 消息类型枚举
 *
 * @author wk
 * @date 2023/1/25 21:48
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    MESSAGE_SYSTEM(0,"系统消息"),
    MESSAGE_PRIVATE(1,"私聊消息"),
    MESSAGE_GROUP(2,"群聊消息"),
    MESSAGE_CLOSE(3,"断开链接");

    private final Integer code;
    private final String info;

}
