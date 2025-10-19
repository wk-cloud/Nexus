package com.nexus.websocket.domain;

import com.nexus.common.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;


/**
 * 聊天消息
 *
 * @author wk
 * @date 2023/1/24
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "聊天消息实体类",description = "聊天消息实体类")
public class Message extends BaseEntity implements Serializable {

    @Schema(name = "用户id")
    private Long userId;

    @Schema(name = "发送人名称")
    private String senderName;

    @Schema(name = "接收人名称")
    private String getterName;

    @Schema(name = "消息内容")
    private String content;

    @Schema(name = "消息类型")
    private Integer messageType;

    @Schema(name = "在线用户数量")
    private Long onLineUserCount;

    @Schema(name = "发送时间")
    private LocalDateTime sendTime;

    @Schema(name = "在线用户集合")
    private Set<String> onLineUserSet;

}
