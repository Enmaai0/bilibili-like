package com.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.UserActivity;
import com.bilibili.domain.annotation.DeniedUser;
import com.bilibili.domain.constant.AuthRoleConstant;
import com.bilibili.service.UserActivityService;
import com.bilibili.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserActivityApi {
    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private UserSupport userSupport;

    @DeniedUser(value = {AuthRoleConstant.ROLE_LV0})
    @PostMapping("user-activity")
    public JsonResponse<String> addUserActivity(@RequestBody UserActivity userActivity) {
        userActivity.setUserId(userSupport.getCurrentUserId());
        return userActivityService.addUserActivity(userActivity);
    }

    @GetMapping("user-subscribed-activity")
    public JsonResponse<List<UserActivity>> getUserActivity() {
        Long userId = userSupport.getCurrentUserId();
        return JsonResponse.success(userActivityService.getUserSubscribedActivity(userId));
    }
}
