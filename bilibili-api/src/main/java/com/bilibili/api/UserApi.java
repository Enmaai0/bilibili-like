package com.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.*;
import com.bilibili.service.UserFollowingService;
import com.bilibili.service.UserService;
import com.bilibili.service.util.RSAUtil;
import com.bilibili.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserApi {
    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(){
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return JsonResponse.success(user);
    }

    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String publicKeyStr = RSAUtil.getPublicKeyStr();
        return JsonResponse.success(publicKeyStr);
    }

    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success("User added successfully");
    }

    @PostMapping("user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return JsonResponse.success(token);
    }

    @PutMapping("/users")
    public JsonResponse<String> updateUserInfo(@RequestBody User user) {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUserInfo(user);
        return JsonResponse.success("User info updated successfully");
    }

    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer pageNum, @RequestParam Integer size, String nick) {
        Long userId = userSupport.getCurrentUserId();
        JSONObject map = new JSONObject();
        map.put("userId", userId);
        map.put("nick", nick);
        map.put("pageNum", pageNum);
        map.put("size", size);
        PageResult<UserInfo> pageResult = userService.pageListUserInfos(map);

        if(pageResult.getTotal() > 0) {
            List<UserInfo> resultList = userFollowingService.checkFollowingStatus(pageResult.getItems(), userId);
            pageResult.setItems(resultList);
        }

        return JsonResponse.success(pageResult);
    }
}
