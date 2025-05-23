package com.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.PageResult;
import com.bilibili.domain.Video;
import com.bilibili.service.VideoService;
import com.bilibili.support.UserSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class VideoApi {
    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video) {
        // Set the user ID from the UserSupport
        video.setUserId(userSupport.getCurrentUserId());

        // Call the service to add the video
        videoService.addVideo(video);

        // Return a success response
        return JsonResponse.success();
    }

    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer pageNum,
                                                          Integer pageSize,
                                                          String area) {
        PageResult<Video> pageResult = videoService.pageListVideos(pageNum, pageSize, area);

        return JsonResponse.success(pageResult);
    }

    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @RequestParam("url") String url) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, url);
    }
}
