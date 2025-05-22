package com.bilibili.dao;

import com.bilibili.domain.Video;
import com.bilibili.domain.VideoTag;

import java.util.List;
import java.util.Map;

public interface VideoMapper {
    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    Integer pageCountVideos(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);
}
