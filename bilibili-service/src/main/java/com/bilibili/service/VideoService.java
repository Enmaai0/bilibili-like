package com.bilibili.service;

import com.bilibili.dao.VideoMapper;
import com.bilibili.domain.*;
import com.bilibili.service.exception.ConditionalException;
import com.bilibili.service.util.LocalFileStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {
    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private LocalFileStorage localFileStorage;
    @Autowired
    private UserCoinService userCoinService;

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

    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String url) throws Exception {
        // 处理url路径，提取文件名
        String fileName = url;
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        if (fileName.startsWith("video/")) {
            fileName = fileName.substring(6);
        }
        
        // 打印调试信息
        System.out.println("Storage base path: " + localFileStorage.getStorageBasePath());
        System.out.println("File name: " + fileName);
        
        // 构建完整的文件路径
        File file = new File(localFileStorage.getStorageBasePath(), fileName);
        System.out.println("Full file path: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());
        
        if (!file.exists()) {
            throw new ConditionalException("Video file not found: " + fileName);
        }

        long fileLength = file.length();
        String rangeHeader = request.getHeader("Range");
        
        // Set response headers
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        
        if (StringUtils.hasText(rangeHeader)) {
            // Parse range header
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileLength - 1;
            
            // Validate range
            if (start >= fileLength || end >= fileLength) {
                response.setHeader("Content-Range", "bytes */" + fileLength);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            
            // Set partial content response headers
            long contentLength = end - start + 1;
            response.setHeader("Content-Length", String.valueOf(contentLength));
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            
            // Stream the range
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                byte[] buffer = new byte[8192];
                randomAccessFile.seek(start);
                long remaining = contentLength;
                
                while (remaining > 0) {
                    int read = randomAccessFile.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (read == -1) break;
                    response.getOutputStream().write(buffer, 0, read);
                    remaining -= read;
                }
            }
        } else {
            // Stream entire file
            response.setHeader("Content-Length", String.valueOf(fileLength));
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = bis.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, read);
                }
            }
        }
    }

    public void addVideoLike(Long videoId, Long userId) {
        Video video = videoMapper.getVideoById(videoId);

        if(video == null) {
            throw new ConditionalException("Video not found");
        }

        Long likeId = videoMapper.getVideoIdByVideoIdAndUserId(videoId, userId);
        if(likeId != null) {
            throw new ConditionalException("Video already liked");
        }

        VideoLike videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoMapper.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoMapper.deleteVideoLike(videoId, userId);
    }

    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        Integer count = videoMapper.countVideoLikes(videoId);
        // Check if the user has liked the video
        Long liked = videoMapper.getVideoIdByVideoIdAndUserId(videoId, userId);
        Map<String, Object> result = Map.of(
                "count", count,
                "liked", liked != null
        );
        return result;
    }

    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if(videoId == null || groupId == null) {
            throw new ConditionalException("Invalid parameters");
        }

        Video video = videoMapper.getVideoById(videoId);
        if(video == null) {
            throw new ConditionalException("Video not found");
        }

        videoMapper.deleteVideoCollection(videoId, userId);
        videoCollection.setUserId(userId);
        videoMapper.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long videoId, Long userId) {
        videoMapper.deleteVideoCollection(videoId, userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Integer count = videoMapper.countVideoCollections(videoId);
        VideoCollection collected = videoMapper.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        return Map.of(
                "count", count,
                "collected", collected != null
        );
    }

    @Transactional
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if(videoId == null){
            throw new ConditionalException("Invalid parameters");
        }
        Video video = videoMapper.getVideoById(videoId);
        if(video == null){
            throw new ConditionalException("Video not found");
        }

        //check if the user has enough coins
        Integer userCoinsAmount = userCoinService.getUserCoinAmount(userId);
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        if(amount > userCoinsAmount){
            throw new ConditionalException("Insufficient coins");
        }

        //check if the user has already given coins to this video
        VideoCoin dbVideoCoin = videoMapper.getVideoCoinByVideoIdAndUserId(videoId, userId);
        //update video coin amount
        if(dbVideoCoin == null){
            videoCoin.setUserId(userId);
            videoMapper.addVideoCoin(videoCoin);
        }else{
            Integer dbAmount = dbVideoCoin.getAmount();
            dbAmount += amount;
            //update video coin amount
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoMapper.updateVideoCoin(videoCoin);
        }

        //update user coins amount
        userCoinService.updateUserCoinsAmount(userId, (userCoinsAmount-amount));
    }

    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        Integer count = videoMapper.getVideoCoinsAmount(videoId);
        VideoCoin videoCollection = videoMapper.getVideoCoinByVideoIdAndUserId(videoId, userId);
        Map<String, Object> result = Map.of(
                "count", count,
                "like", videoCollection != null
        );
        return result;
    }
}
