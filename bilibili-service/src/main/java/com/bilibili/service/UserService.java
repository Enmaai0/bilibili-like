package com.bilibili.service;

import ch.qos.logback.core.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.bilibili.dao.UserMapper;
import com.bilibili.domain.PageResult;
import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.constant.UserConstant;
import com.bilibili.service.exception.ConditionalException;
import com.bilibili.service.util.MD5Util;
import com.bilibili.service.util.RSAUtil;
import com.bilibili.service.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void addUser(User user) {
        String phone = user.getPhone();
        if(StringUtil.isNullOrEmpty(phone)) {
            throw new ConditionalException("Phone number is required");
        }
        User dbUser = userMapper.getUserByPhone(phone);
        if(dbUser != null) {
            throw new ConditionalException("Phone number already exists");
        }

        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionalException("Failed to decrypt password");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);

        Integer count = userMapper.addUser(user);
        if(count == 0) {
            throw new ConditionalException("Failed to add user");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        count = userMapper.addUserInfo(userInfo);
        if(count == 0) {
            throw new ConditionalException("Failed to add user info");
        }
    }

    public String login(User user) throws Exception {
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();

        if(StringUtil.isNullOrEmpty(phone) && StringUtil.isNullOrEmpty(email)) {
            throw new ConditionalException("Phone number or email is required");
        }
        String pe = phone + email;
        User dbUser = userMapper.getUserByPhoneOrEmail(pe);
        if(dbUser == null) {
            throw new ConditionalException("User does not exist");
        }

        String password = user.getPassword();
        String salt = dbUser.getSalt();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionalException("Failed to decrypt password");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!dbUser.getPassword().equals(md5Password)) {
            throw new ConditionalException("Incorrect password");
        }

        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
        User user = userMapper.getUserById(userId);
        UserInfo userInfo = userMapper.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public List<UserInfo> getUserInfoByUserIds(List<Long> userIds) {
        return userMapper.getUserInfoByUserIds(userIds);
    }

    public void updateUserInfo(User user) {
        Long userId = user.getId();
        if(userId == null) {
            throw new ConditionalException("User ID is required");
        }
        User dbUser = userMapper.getUserById(userId);
        if(dbUser == null) {
            throw new ConditionalException("User does not exist");
        }

        if(!StringUtil.isNullOrEmpty(user.getPassword())) {
            String password = user.getPassword();
            String salt = dbUser.getSalt();
            String rawPassword;
            try {
                rawPassword = RSAUtil.decrypt(password);
            } catch (Exception e) {
                throw new ConditionalException("Failed to decrypt password");
            }

            String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
            user.setPassword(md5Password);
        }

        userMapper.updateUser(user);
        return;
    }

    public User getUserById(Long userId) {
        return userMapper.getUserById(userId);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject map) {
        Integer pageNum = map.getInteger("pageNum");
        Integer size = map.getInteger("size");
        int start = (pageNum - 1) * size;
        map.put("start", start);
        map.put("limit", size);
        int total = userMapper.pageCountUserInfos(map);
        List<UserInfo> list = new ArrayList<>();
        if(total > 0) {
            list = userMapper.pageListUserInfos(map);
        }
        return new PageResult<>(list, total);
    }
}
