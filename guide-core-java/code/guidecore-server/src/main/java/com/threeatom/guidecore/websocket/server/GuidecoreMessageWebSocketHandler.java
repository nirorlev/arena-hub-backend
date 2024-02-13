package com.threeatom.guidecore.websocket.server;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.util.I18NUtil;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GuidecoreMessageWebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GuidecoreMessageWebSocketHandler.class);

    private static int onlineCount = 0;

    private static ConcurrentHashMap<Integer, WebSocketSession> webSocketMap =
            new ConcurrentHashMap<Integer, WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO Auto-generated method stub
        super.afterConnectionEstablished(session);

        Integer userAccessId = (Integer) session.getAttributes().get("userAccessId");

        if (webSocketMap.containsKey(userAccessId)) {
            webSocketMap.remove(userAccessId);
            webSocketMap.put(userAccessId, session);
        } else {
            webSocketMap.put(userAccessId, session);
            addOnlineCount();
        }

        LOGGER.info("用户连接:" + userAccessId + ",当前在线人数为:" + getOnlineCount());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // TODO Auto-generated method stub
        super.afterConnectionClosed(session, status);

        Integer userAccessId = (Integer) session.getAttributes().get("userAccessId");
        webSocketMap.remove(userAccessId);
        subOnlineCount();
        LOGGER.info("用户退出:" + userAccessId + ",当前在线人数为:" + getOnlineCount());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // TODO Auto-generated method stub
        super.handleTextMessage(session, message);

        String msg = message.getPayload();

        LOGGER.info("msg:" + msg);
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public static void sendMessageToUser(Integer userAccessId, TextMessage message) {
        WebSocketSession session = webSocketMap.get(userAccessId);

        if (session == null) return;
        if (!session.isOpen()) throw new SystemException(I18NUtil.get("websocket.error"));

        try {
            session.sendMessage(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        GuidecoreMessageWebSocketHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        GuidecoreMessageWebSocketHandler.onlineCount--;
    }
}
