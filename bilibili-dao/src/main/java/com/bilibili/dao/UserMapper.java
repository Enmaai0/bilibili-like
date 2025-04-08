package com.bilibili.dao;

import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;

public interface UserMapper {
    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long userId);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUser(User user);

    User getUserByPhoneOrEmail(String phoneOrEmail);
}
