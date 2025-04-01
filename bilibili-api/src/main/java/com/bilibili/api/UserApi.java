package com.bilibili.api;

import com.bilibili.domain.User;
import com.bilibili.domain.JsonResponse;
import com.bilibili.service.UserService;
import com.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApi {
    @Autowired
    private UserService userService;

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
    public JsonResponse<String> login(@RequestBody User user) {
        String token = userService.login(user);
        return JsonResponse.success(token);
    }
}
