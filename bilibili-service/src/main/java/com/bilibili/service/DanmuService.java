package com.bilibili.service;

import ch.qos.logback.core.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.bilibili.dao.DanmuMapper;
import com.bilibili.domain.Danmu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DanmuService {
    @Autowired
    DanmuMapper danmuMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    
    public void addDanmu(Danmu danmu) {
        danmuMapper.addDanmu(danmu);
    }

    @Async
    public void asyncAddDanmu(Danmu danmu){
        danmuMapper.addDanmu(danmu);
    }

    public List<Danmu> getDanmus(Map<String, Object> params) {
        return danmuMapper.getDanmus(params);
    }

    public void addDanmuToRedis(Danmu danmu) {
        String key = "danmu-video-" + danmu.getVideoId();
        String value = (String) redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();
        if(!StringUtil.isNullOrEmpty(value)) {
            list = JSONArray.parseArray(value, Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key, JSONArray.toJSONString(list));
    }
}
