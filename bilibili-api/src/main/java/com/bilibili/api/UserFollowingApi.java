package com.bilibili.api;

import com.bilibili.domain.FollowingGroup;
import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.UserFollowing;
import com.bilibili.service.UserFollowingService;
import com.bilibili.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserFollowingApi {
    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserSupport userSupport;

    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowing(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowing(userFollowing);
        return JsonResponse.success("Followed");
    }

    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return JsonResponse.success(result);
    }

    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> result = userFollowingService.getFans(userId);
        return JsonResponse.success(result);
    }

    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroup(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroup(followingGroup);
        return JsonResponse.success(groupId);
    }

    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowingGroups(userId);
        return JsonResponse.success(result);
    }
}
