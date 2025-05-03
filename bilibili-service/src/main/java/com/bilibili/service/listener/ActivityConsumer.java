package com.bilibili.service.listener;

import ch.qos.logback.core.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.UserActivity;
import com.bilibili.domain.UserFollowing;
import com.bilibili.domain.constant.UserActivityConstant;
import com.bilibili.service.UserFollowingService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RocketMQMessageListener(
        topic = UserActivityConstant.TOPIC_ACTIVITY,
        consumerGroup = UserActivityConstant.GROUP_ACTIVITY
)
public class ActivityConsumer implements RocketMQListener<String> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    @Override
    public void onMessage(String msg) {
        UserActivity userActivity = JSONObject.parseObject(msg, UserActivity.class);
        Long userId = userActivity.getUserId();
        List<UserFollowing> fans = userFollowingService.getFans(userId);
        for(UserFollowing fan : fans) {
            String key = "subscribed_" + fan.getUserId();
            String value = redisTemplate.opsForValue().get(key);
            List<UserActivity> suscribedList;
            if(StringUtil.isNullOrEmpty(value)) {
                suscribedList = new ArrayList<>();
            } else {
                suscribedList = JSONArray.parseArray(value, UserActivity.class);
            }
            suscribedList.add(userActivity);
            redisTemplate.opsForValue().set(key, JSONArray.toJSONString(suscribedList));
        }
    }
}
