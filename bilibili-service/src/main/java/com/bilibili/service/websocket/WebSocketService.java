package com.bilibili.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.Danmu;
import com.bilibili.domain.constant.UserActivityConstant;
import com.bilibili.service.DanmuService;
import com.bilibili.service.util.RocketMQUtil;
import com.bilibili.service.util.TokenUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/imserver/{token}")
public class WebSocketService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    public static final ConcurrentHashMap<String, WebSocketService> webSocketMap = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    private static ApplicationContext applicationContext;

    private Long userId;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.applicationContext = applicationContext;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try{
            this.userId = TokenUtil.verifyToken(token);
        } catch (Exception ignored) {}
        this.session = session;
        this.sessionId = session.getId();
        if(webSocketMap.containsKey(sessionId)) {
            webSocketMap.remove(sessionId);
        } else {
            webSocketMap.put(sessionId, this);
            onlineCount.getAndIncrement();
        }
        logger.info("New connection established, sessionId: {}, current online count: {}", sessionId, onlineCount.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("Failed to send message to client: {}", e.getMessage());
            webSocketMap.remove(sessionId);
            onlineCount.decrementAndGet();
        }
    }

    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(sessionId)) {
            webSocketMap.remove(sessionId);
            onlineCount.getAndDecrement();
            logger.info("Connection closed, sessionId: {}, current online count: {}", sessionId, onlineCount.get());
        }
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("userInfo: {}, message: {}", sessionId, message);
        // send message to all connected clients
        for (WebSocketService webSocket : webSocketMap.values()) {
            JSONObject msgBody = new JSONObject();
            msgBody.put("message", message);
            msgBody.put("sessionId", sessionId);

            if(applicationContext != null) {
                try {
                    RocketMQUtil rocketMQUtil = applicationContext.getBean(RocketMQUtil.class);
                    rocketMQUtil.asyncSendMsg(UserActivityConstant.TOPIC_DANMU, msgBody.toJSONString());
                } catch (Exception e) {
                    logger.error("Failed to send message to RocketMQ: {}", e.getMessage());
                }
            }
        }
        if(userId != null) {
            // Save danmu to database
            Danmu danmu = JSONObject.parseObject(message, Danmu.class);
            danmu.setUserId(userId);
            DanmuService danmuService = (DanmuService) applicationContext.getBean("danmuService");
            danmuService.asyncAddDanmu(danmu);
            // Save danmu to Redis
            danmuService.addDanmuToRedis(danmu);
        }
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("WebSocket error: {}", error.getMessage());
        if (webSocketMap.containsKey(sessionId)) {
            webSocketMap.remove(sessionId);
            onlineCount.getAndDecrement();
            logger.info("Connection error, sessionId: {}, current online count: {}", sessionId, onlineCount.get());
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public Session getSession() {
        return session;
    }
}
