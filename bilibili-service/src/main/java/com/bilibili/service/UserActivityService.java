package com.bilibili.service;

import ch.qos.logback.core.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.bilibili.dao.UserActivityMapper;
import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.UserActivity;
import com.bilibili.domain.constant.UserActivityConstant;
import com.bilibili.service.util.RocketMQUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityService {
    @Autowired
    private UserActivityMapper userActivityMapper;

    @Autowired
    private RocketMQUtil rocketMQUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public JsonResponse<String> addUserActivity(UserActivity userActivity) {
        userActivityMapper.addUserActivity(userActivity);
        rocketMQUtil.syncSendMsg(UserActivityConstant.TOPIC_ACTIVITY, userActivity);
        return JsonResponse.success();
    }

    public List<UserActivity> getUserSubscribedActivity(Long userId) {
        String key = "subscribed_" + userId;
        String value = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(value, UserActivity.class);
    }
}
