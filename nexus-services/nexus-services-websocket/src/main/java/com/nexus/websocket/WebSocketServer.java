package com.nexus.websocket;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.TokenUtils;
import com.nexus.websocket.domain.Message;
import com.nexus.websocket.enums.MessageTypeEnum;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketServer
 *
 * @author wk
 * @date 2023/1/24
 */
@Component
@ServerEndpoint(value = "/ws/{token}")
@Slf4j
public class WebSocketServer {

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 用来管理每个客户端的WebSocketServer对象
     */
    private static final ConcurrentHashMap<String, WebSocketServer> socketMap = new ConcurrentHashMap<>();

    /**
     * 最大连接数
     */
    private static final Long MAX_CONNECT_COUNT = 10000L;

    /**
     * 建立 WebSocket 连接
     *
     * @param session 会话
     */
    @OnOpen
    public void open(Session session, @PathParam("token") String token) {
        if(TokenUtils.isExpired(token)){
            log.error("====> 登录已过期，请重新登录");
            return;
        }
        String userId = (String)TokenUtils.getValueFromToken(token, "userId");
        log.info("====> WebSocket建立连接中，连接用户ID：{}", userId);
        if (MAX_CONNECT_COUNT <= socketMap.size()) {
            log.error("WebSocket连接数超过最大连接数，无法建立连接");
            try {
                session.close();
            } catch (IOException e) {
                log.error("WebSocket连接数超过最大连接数，无法建立连接。错误信息：{}", e.getMessage());
            }
            return;
        }
        WebSocketServer webSocketServer = socketMap.get(userId);
        if (ObjectUtils.isNotNull(webSocketServer)) {
            try {
                socketMap.remove(userId);
                webSocketServer.session.close();
                log.warn("====> 重复登录，被挤掉的用户ID：{}", userId);
            } catch (IOException e) {
                log.error("重复登录异常,错误信息：{}", e.getMessage());
            }
        }
        // 建立连接
        this.session = session;
        socketMap.put(userId, this);
        // 1. 推送在线人数
        Message message = new Message();
        message.setOnLineUserCount(getOnLineUserCount());
        message.setMessageType(MessageTypeEnum.MESSAGE_GROUP.getCode());
        message.setOnLineUserSet(getOnlineUsers());
        pushMessage(JSON.toJSONString(message));
        log.info("====> 建立WebSocket连接成功，当前在线人数：{}", getOnLineUserCount());
    }

    /**
     * 接收客户端消息
     *
     * @param message 消息
     */
    @OnMessage
    public void message(String message) {
        JSONObject messageObject = JSON.parseObject(message);
        String messageTypeStr = messageObject.getString("messageType");
        Integer messageType = Integer.valueOf(messageTypeStr);

        if (MessageTypeEnum.MESSAGE_PRIVATE.getCode().equals(messageType)) {
            // 发送私聊信息
        } else if (MessageTypeEnum.MESSAGE_GROUP.getCode().equals(messageType)) {
            // 发送群消息
            Message newMessage = new Message();
            newMessage.setMessageType(MessageTypeEnum.MESSAGE_GROUP.getCode());
            // TODO: 补充其他字段设置
            sendGroupMessage(newMessage);
        }
    }

    /**
     * 错误处理
     *
     * @param throwable throwable
     */
    @OnError
    public void error(Throwable throwable) {
        log.error("WebSocket连接异常，异常信息：{}", throwable.getMessage(), throwable);
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void close(@PathParam("userId") String userId) {
        log.info("====> 用户：{} 断开连接", userId);
        remove(userId);
        log.info("====> 连接断开，当前在线人数为：{}", socketMap.size());
    }

    /**
     * 移除连接会话
     *
     * @param key 键
     * @return boolean
     */
    public static boolean remove(String key) {
        return socketMap.remove(key) != null;
    }

    /**
     * 移除连接会话
     *
     * @param session 会期
     * @return boolean
     */
    public static boolean remove(Session session) {
        Iterator<Map.Entry<String, WebSocketServer>> iterator = socketMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WebSocketServer> entry = iterator.next();
            if (entry.getValue().session.equals(session)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * 获取在线用户数量
     */
    public static Long getOnLineUserCount() {
        return (long) socketMap.size();
    }

    /**
     * 获取在线用户
     *
     * @return {@link Set}<{@link String}>
     */
    public static Set<String> getOnlineUsers() {
        return socketMap.keySet();
    }

    /**
     * 推送消息
     *
     * @param message 消息
     */
    public static void pushMessage(String message) {
        for (WebSocketServer server : socketMap.values()) {
            try {
                server.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("====> 消息推送异常：{}", e.getMessage());
            }
        }
    }

    /**
     * 发送群消息
     *
     * @param message 消息
     */
    public static void sendGroupMessage(String message) {
        pushMessage(message);
    }

    /**
     * 发送群消息
     *
     * @param message 消息
     */
    public static void sendGroupMessage(Message message) {
        log.info("====> 发送群消息：{}", message);
        String jsonMsg = JSON.toJSONString(message);
        for (WebSocketServer server : socketMap.values()) {
            try {
                server.session.getBasicRemote().sendText(jsonMsg);
            } catch (IOException e) {
                log.error("====> 群消息发送异常：{}", e.getMessage());
            }
        }
    }

    /**
     * 发送私信
     *
     * @param message 消息
     */
    public static void sendPrivateMessage(Message message) {
        log.info("====> 用户ID: {} 发送消息给：{} ：{}", message.getSenderName(), message.getGetterName(), message.getContent());
        WebSocketServer target = socketMap.get(message.getUserId());
        if (target == null) {
            log.warn("目标用户 {} 不在线", message.getUserId());
            return;
        }
        try {
            target.session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
            log.error("====> 消息发送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送私信
     *
     * @param userId  用户 ID
     * @param message 消息
     */
    public static void sendPrivateMessage(String userId, String message) {
        log.info("====> 用户ID: {} 发送消息给：{}", userId, message);
        WebSocketServer target = socketMap.get(userId);
        if (target == null) {
            log.warn("目标用户 {} 不在线", userId);
            return;
        }
        try {
            target.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("====> 消息发送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送私信
     *
     * @param session 会期
     * @param message 消息
     */
    public static void sendPrivateMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("====> 消息发送异常：{}", e.getMessage());
        }
    }
}
