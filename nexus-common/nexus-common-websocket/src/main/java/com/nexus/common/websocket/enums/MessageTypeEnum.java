package com.nexus.common.websocket.enums;

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

    MESSAGE_SYSTEM(1,"系统消息"),
    MESSAGE_PRIVATE(2,"私聊消息"),
    MESSAGE_GROUP(3,"群聊消息"),
    MESSAGE_ONLINE(4,"用户上线通知"),
    MESSAGE_OFFLINE(5,"用户正常下线通知"),
    MESSAGE_FORCE_OFFLINE(6,"用户强制下线通知");

    private final Integer code;
    private final String info;

}
