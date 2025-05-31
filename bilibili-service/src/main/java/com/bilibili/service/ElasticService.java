package com.bilibili.service;

import com.bilibili.dao.repository.VideoRepository;
import com.bilibili.domain.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticService {
    @Autowired
    private VideoRepository videoRepository;

    public void add(Video video) {
        videoRepository.save(video);
    }

    public Video getVideos(String keyword) {
        Video video = videoRepository.findByTitleLike(keyword);
        return video;
    }
}
