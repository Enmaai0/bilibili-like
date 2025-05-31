package com.bilibili.service;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.bilibili.dao.repository.UserInfoRepository;
import com.bilibili.dao.repository.VideoRepository;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ElasticService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    public Video getVideos(String keyword) {
        Video video = videoRepository.findByTitleLike(keyword);
        return video;
    }

    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }
}
