package com.nexus.common.websocket.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 聊天消息
 *
 * @author wk
 * @date 2023/1/24
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message implements Serializable {

    /**
     * 发送人id
     */
    private Long senderUserId;

    /**
     * 发送人名称
     */
    private String senderName;

    /**
     * 接收人id
     */
    private Long getterUserId;

    /**
     * 接收人名称
     */
    private String getterName;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息内容类型
     */
    private Integer contentType;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

}
