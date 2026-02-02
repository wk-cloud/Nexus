package com.nexus.websocket;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson2.JSON;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.common.utils.TokenUtils;
import com.nexus.websocket.domain.Message;
import com.nexus.websocket.enums.MessageTypeEnum;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocketServer
 *
 * @author wk
 * @date 2023/1/24
 */
@CrossOrigin
@Component
@Getter
@Slf4j
@ServerEndpoint(value = "/ws/{token}")
public class WebSocketServer {

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * ping信息（心跳检测）
     */
    private final String PING_MESSAGE = "ping";

    /**
     * 回复给ping的信息（心跳检测）
     */
    private final String PONG_MESSAGE = "pong";

    /**
     * 用来管理每个客户端的WebSocketServer对象
     * key：用户id
     * value：WebSocketServer对象
     */
    private static final ConcurrentHashMap<Long, WebSocketServer> socketMap = new ConcurrentHashMap<>();

    /**
     * 最大连接数
     */
    private static final Long maxConnectCount = 10000L;

    /**
     * 判断是否是 json 字符串
     *
     * @param str 待检测字符串
     * @return boolean
     */
    private static boolean isJsonStr(String str) {
        return JSON.isValid(str) || JSON.isValidObject(str);
    }

    /**
     * 判断是否在线
     *
     * @param userId 用户id
     * @return boolean
     */
    public static boolean isOnline(Long userId) {
        return ObjectUtils.isNotNull(socketMap.get(userId)) && isOpen(socketMap.get(userId));
    }

    /**
     * 判断是否在线
     *
     * @param webSocketServer websocket服务
     * @return boolean
     */
    public static boolean isOnline(WebSocketServer webSocketServer) {
        return ObjectUtils.isNotNull(webSocketServer) && isOpen(webSocketServer);
    }

    /**
     * 判断是否在线
     *
     * @param session websocket 会话
     * @return boolean
     */
    public static boolean isOnline(Session session) {
        return getOnlineSessions().contains(session);
    }

    /**
     * 判断WebSocket会话是否处于打开状态
     *
     * @param session 会话
     * @return boolean
     */
    public static boolean isOpen(Session session) {
        return ObjectUtils.isNotNull(session) && session.isOpen();
    }

    /**
     * 判断 WebSocket 会话是否处于打开状态
     *
     * @param webSocketServer websocket服务
     * @return boolean
     */
    public static boolean isOpen(WebSocketServer webSocketServer) {
        return ObjectUtils.isNotNull(webSocketServer) && ObjectUtils.isNotNull(webSocketServer.session) && webSocketServer.session.isOpen();
    }

    /**
     * 建立 WebSocket 连接
     *
     * @param session 会话
     * @param token   令牌
     */
    @OnOpen
    public void open(Session session, @PathParam("token") String token) {
        String userIdStr = TokenUtils.getValueFromToken(token, "userId");
        if (StringUtils.isBlank(userIdStr)) {
            log.error("====> WebSocket 连接建立失败，失败原因：用户未登录");
            closeSession(session, CloseReason.CloseCodes.VIOLATED_POLICY, "用户未登录");
            return;
        }
        if (maxConnectCount < socketMap.size()) {
            log.error("====> WebSocket 已达到最大连接数");
            closeSession(session, CloseReason.CloseCodes.TRY_AGAIN_LATER, "服务器繁忙，请稍后重试");
            return;
        }
        Long userId = Long.valueOf(userIdStr);
        WebSocketServer webSocketServer = socketMap.get(userId);
        if (ObjectUtils.isNotNull(webSocketServer)) {
            Session oldSession = webSocketServer.session;
            // 1. 发送强制下线通知
            sendForceOfflineNotice(userId, null);
            // 2. 关闭旧连接
            closeSession(oldSession, CloseReason.CloseCodes.VIOLATED_POLICY, "账号在其他地方登录");
            // 3. 移除旧连接
            socketMap.remove(userId);
            log.warn("====> 账号在其他地方登录，用户[{}]被强制下线", userId);
        }
        // 建立连接
        this.session = session;
        socketMap.put(userId, this);
        // 用户上线通知
        this.sendOnlineNotice(userId);
        log.info("====> WebSocket 连接成功，当前在线人数：{}", getOnLineUserCount());
    }

    /**
     * 接收客户端消息
     *
     * @param session 会话
     * @param message 消息
     */
    @OnMessage
    public void message(Session session, String message) {
        boolean isJson = isJsonStr(message);
        if (!isJson && !PING_MESSAGE.equals(message)) {
            log.error("====> 消息推送失败，消息内容必须是 json 格式：{}", message);
            return;
        }
        if (PING_MESSAGE.equals(message)) {
            log.info("====> WebSocket session：{}, {}", session, message);
            // 推送心跳检测信息
            sendPrivateMessage(session, PONG_MESSAGE);
            return;
        }
        Message chatMessage = JSON.parseObject(message, Message.class);
        Integer messageType = chatMessage.getMessageType();
        if (MessageTypeEnum.MESSAGE_PRIVATE.getCode().equals(messageType)) {
            // 发送私聊信息
            sendPrivateMessage(chatMessage);
        } else if (MessageTypeEnum.MESSAGE_GROUP.getCode().equals(messageType)) {
            // 发送群消息
            Map.Entry<Long, WebSocketServer> socketServerEntry = socketMap.entrySet()
                    .stream()
                    .filter(entry -> ObjectUtils.isNotNull(entry.getValue())
                            && ObjectUtils.isNotNull(entry.getValue().getSession())
                            && entry.getValue().getSession().equals(session))
                    .findFirst()
                    .orElse(null);
            if (ObjectUtils.isNotNull(socketServerEntry)) {
                chatMessage.setSenderUserId(socketServerEntry.getKey());
            }
            sendGroupMessage(chatMessage);
        }
    }


    /**
     * 错误处理
     *
     * @param throwable throwable
     */
    @OnError
    public void error(Throwable throwable) {
        clearExpiredWebsocketServer();
        log.error("====> WebSocket 发生错误，错误信息：{}", throwable.getMessage());
    }

    /**
     * 关闭连接
     *
     * @param session 会话
     * @param token   令牌
     */
    @OnClose
    public void close(Session session, @PathParam("token") String token) {
        try {
            String userIdStr = TokenUtils.getValueFromToken(token, "userId");
            if (StringUtils.isBlank(userIdStr)) {
                log.error("====> WebSocket 连接关闭失败，失败原因：用户未登录");
                closeSession(session, CloseReason.CloseCodes.VIOLATED_POLICY, "用户未登录");
                return;
            }
            Long userId = Long.valueOf(userIdStr);
            // 1. 关闭连接会话
            closeSession(session, CloseReason.CloseCodes.NORMAL_CLOSURE, "正常关闭连接");
            // 2. 移除连接会话
            remove(userId);
            log.info("====> 用户[{}]断开连接", userId);
            log.info("====> 当前在线人数：{}", socketMap.size());
        } catch (Exception e) {
            log.error("====> WebSocket 连接关闭失败，失败原因：{}", e.getMessage());
            closeSession(session, CloseReason.CloseCodes.VIOLATED_POLICY, e.getMessage());
        }
    }

    /**
     * 关闭 WebSocket 会话
     */
    private void closeSession(Session session, CloseReason.CloseCode closeCode, String reason) {
        try {
            if (ObjectUtils.isNull(session)) {
                return;
            }
            if (session.isOpen()) {
                session.close(new CloseReason(closeCode, reason));
            }
        } catch (Exception e) {
            log.debug("====> WebSocket 连接关闭失败，失败原因：{}", e.getMessage());
        }
    }

    /**
     * 移除连接会话
     *
     * @param userId 用户id
     * @return boolean
     */
    public static boolean remove(Long userId) {
        return !socketMap.containsKey(userId) || socketMap.remove(userId) != null;
    }

    /**
     * 移除连接会话
     *
     * @param session 会话
     * @return boolean
     */
    public static boolean remove(Session session) {
        if (ObjectUtils.isNull(session)) {
            log.error("====> 会话移除失败，失败原因：{}", "session is null");
            return false;
        }
        Set<Session> sessions = socketMap.values().stream().map(WebSocketServer::getSession).collect(Collectors.toSet());
        if (sessions.contains(session)) {
            for (Map.Entry<Long, WebSocketServer> entry : socketMap.entrySet()) {
                WebSocketServer value = entry.getValue();
                if (ObjectUtils.isNotNull(value) && session.equals(value.getSession())) {
                    return remove(entry.getKey());
                }
            }
        }
        return true;
    }

    /**
     * 清除不在线websocket服务
     */
    public static void clearExpiredWebsocketServer() {
        socketMap.entrySet().removeIf(entry -> !isOnline(entry.getValue()));
    }

    /**
     * 心跳检测
     */
    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void heartbeatCheck() {
        log.info("====> WebSocketServer 心跳检测");
        clearExpiredWebsocketServer();
    }

    /**
     * 获取在线用户集合
     */
    public static Set<Long> getOnLineUsers() {
        return socketMap.keySet().stream().filter(WebSocketServer::isOnline).collect(Collectors.toSet());
    }

    /**
     * 获取在线用户数量
     */
    public static Long getOnLineUserCount() {
        return Long.parseLong(Integer.toString(getOnLineUsers().size()));
    }

    /**
     * 获取在线会话集合
     *
     * @return {@link Set }<{@link Session }>
     */
    public static Set<Session> getOnlineSessions() {
        return socketMap.values().stream()
                .map(WebSocketServer::getSession)
                .filter(WebSocketServer::isOpen)
                .collect(Collectors.toSet());
    }

    /**
     * 发送上线通知 (发送给上线用户)
     *
     * @param onlineUserId 上线用户id
     */
    public void sendOnlineNotice(Long onlineUserId) {
        // 用户上线通知
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.MESSAGE_ONLINE.getCode());
        message.setSenderUserId(onlineUserId);
        sendPrivateMessage(onlineUserId, JSONUtils.toJSONString(message));
    }


    /**
     * 发送上线通知 (发送给群成员)
     *
     * @param onlineUserId 上线用户id
     */
    private void sendOnlineNoticeForGroup(Long onlineUserId) {
        // 用户上线通知
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.MESSAGE_ONLINE.getCode());
        message.setSenderUserId(onlineUserId);
        pushMessage(JSONUtils.toJSONString(message));
    }

    /**
     * 发送下线通知
     *
     * @param offlineUserId 下线用户id
     */
    public void sendOfflineNotice(Long offlineUserId) {
        // 用户下线通知
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.MESSAGE_OFFLINE.getCode());
        message.setSenderUserId(offlineUserId);
        pushMessage(JSONUtils.toJSONString(message));
    }

    /**
     * 发送强制下线通知
     * @param forceOfflineUserId 强制下线用户id
     * @param reason 强制下线原因
     */
    public void sendForceOfflineNotice(Long forceOfflineUserId, String reason) {
        Message message = new Message();
        message.setGetterUserId(forceOfflineUserId);
        message.setMessageType(MessageTypeEnum.MESSAGE_FORCE_OFFLINE.getCode());
        message.setContent(StringUtils.isBlank(reason) ? "您的账号在另一处登录，您已被强制下线" : reason);
        sendPrivateMessage(forceOfflineUserId, message);
    }

    /**
     * 推送消息
     *
     * @param message 消息
     */
    public static void pushMessage(String message) {
        try {
            if (!isJsonStr(message)) {
                log.error("====> 消息推送失败，消息内容必须是 json 格式：{}", message);
                return;
            }
            for (Long userId : socketMap.keySet()) {
                WebSocketServer webSocketServer = socketMap.get(userId);
                if (isOnline(webSocketServer)) {
                    webSocketServer.session.getBasicRemote().sendText(message);
                } else {
                    log.error("====> 消息推送失败，用户[{}]未连接", userId);
                }
            }
        } catch (IOException e) {
            log.error("====> 消息推送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送群消息
     *
     * @param message 消息
     */
    public static void sendGroupMessage(Message message) {
        try {
            log.info("====> 发送群消息：{}", message.getContent());
            for (Long userId : socketMap.keySet()) {
                if (ObjectUtils.isNotNull(message.getSenderUserId()) && message.getSenderUserId().equals(userId)) {
                    continue;
                }
                WebSocketServer webSocketServer = socketMap.get(userId);
                if (isOnline(webSocketServer)) {
                    webSocketServer.session.getBasicRemote().sendText(JSON.toJSONString(message));
                } else {
                    log.error("====> 消息推送失败，用户[{}]未连接", userId);
                }
            }
        } catch (IOException e) {
            log.error("====> 群消息发送异常：{}", e.getMessage());
        }
    }


    /**
     * 发送私信
     *
     * @param message 消息
     */
    public static void sendPrivateMessage(Message message) {
        try {
            log.info("====> 用户[{}]发送消息给[{}]: {}", message.getSenderName(), message.getGetterName(), message.getContent());
            if (ObjectUtils.isNull(message.getGetterUserId())) {
                log.error("====> 私信发送失败，接收者用户id为空");
                return;
            }
            if (!isOnline(socketMap.get(message.getGetterUserId()))) {
                log.error("====> 私信发送失败，接收者连接不存在");
                return;
            }
            socketMap.get(message.getGetterUserId()).session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
            log.error("====> 私息发送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送私信
     *
     * @param getterUserId 接收者用户id
     * @param message      消息
     */
    public static void sendPrivateMessage(Long getterUserId, String message) {
        try {
            if (!isOnline(socketMap.get(getterUserId))) {
                log.error("====> 私信发送失败，接收者连接不存在");
                return;
            }
            socketMap.get(getterUserId).session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("====> 私息发送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送私信
     *
     * @param getterUserId 接收者用户id
     * @param message      消息
     */
    public static void sendPrivateMessage(Long getterUserId, Message message) {
        try {
            if (!isOnline(socketMap.get(getterUserId))) {
                log.error("====> 私信发送失败，接收者连接不存在");
                return;
            }
            socketMap.get(getterUserId).session.getBasicRemote().sendText(JSONUtils.toJSONString(message));
        } catch (IOException e) {
            log.error("====> 私息发送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送私信
     *
     * @param getterSession 接收者会话
     * @param message       消息
     */
    public static void sendPrivateMessage(Session getterSession, Message message) {
        try {
            if (!isOnline(getterSession)) {
                log.error("====> 私信发送失败，接收者连接不存在");
                return;
            }
            getterSession.getBasicRemote().sendText(JSONUtils.toJSONString(message));
        } catch (IOException e) {
            log.error("====> 私息发送异常：{}", e.getMessage());
        }
    }

    /**
     * 发送私信
     *
     * @param getterSession 接收者会话
     * @param message       消息
     */
    public static void sendPrivateMessage(Session getterSession, String message) {
        try {
            if (!isOnline(getterSession)) {
                log.error("====> 私信发送失败，接收者连接不存在");
                return;
            }
            getterSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("====> 私息发送异常：{}", e.getMessage());
        }
    }
}
