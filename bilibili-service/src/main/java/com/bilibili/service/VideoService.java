package com.bilibili.service;

import com.bilibili.dao.VideoMapper;
import com.bilibili.domain.PageResult;
import com.bilibili.domain.Video;
import com.bilibili.domain.VideoTag;
import com.bilibili.service.exception.ConditionalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {
    @Autowired
    private VideoMapper videoMapper;

    @Transactional
    public void addVideo(Video video) {
        Date now = new Date();
        videoMapper.addVideos(video);
        Long videoId = video.getId();

        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(tag -> tag.setVideoId(videoId));

        videoMapper.batchAddVideoTags(tagList);
    }

    public PageResult<Video> pageListVideos(Integer pageNum, Integer pageSize, String area) {
        if(pageSize == null || pageSize <= 0 || pageNum == null || pageNum <= 0) {
            throw new ConditionalException("Invalid parameters");
        }
        Map<String, Object> params = Map.of(
                "start", (pageNum - 1) * pageSize,
                "limit", pageSize,
                "area", area
        );
        List<Video> videoList = new ArrayList<>();
        Integer total = videoMapper.pageCountVideos(params);

        if(total > 0) {
            videoList = videoMapper.pageListVideos(params);

        }

        return new PageResult<>(videoList, total);
    }
}
