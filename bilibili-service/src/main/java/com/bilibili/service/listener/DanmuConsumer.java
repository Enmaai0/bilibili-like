package com.bilibili.service.listener;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.constant.UserActivityConstant;
import com.bilibili.service.websocket.WebSocketService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RocketMQMessageListener(
        topic = UserActivityConstant.TOPIC_DANMU,
        consumerGroup = UserActivityConstant.GROUP_DANMU
)
public class DanmuConsumer implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onMessage(String msg) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(msg);
            String sessionId = jsonObject.getString("sessionId");
            String message = jsonObject.getString("message");

            WebSocketService webSocketService = WebSocketService.webSocketMap.get(sessionId);
            if(webSocketService != null && webSocketService.getSession() != null && webSocketService.getSession().isOpen()) {
                webSocketService.sendMessage(message);
            }
        } catch (IOException e) {
            logger.error("发送弹幕消息失败", e);
        } catch (Exception e) {
            logger.error("处理弹幕消息失败", e);
        }
    }
}