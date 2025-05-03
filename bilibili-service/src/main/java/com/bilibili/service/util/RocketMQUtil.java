package com.bilibili.service.util;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class RocketMQUtil {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void syncSendMsg(String topic, Object payload) {
        SendResult result = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(payload).build());
        logger.info("Sync message sent successfully, messageId: {}", result.getMsgId());
    }

    public void asyncSendMsg(String topic, Object payload) {
        rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(payload).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("Async message sent successfully, messageId: {}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                logger.error("Failed to send async message", throwable);
            }
        });
    }
}