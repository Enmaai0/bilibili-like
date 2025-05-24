package com.bilibili.service;

import com.bilibili.dao.UserCoinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCoinService {
    @Autowired
    UserCoinMapper userCoinMapper;

    public Integer getUserCoinAmount(Long userId) {
        return userCoinMapper.getUserCoinAmount(userId);
    }

    public void updateUserCoinsAmount(Long userId, Integer amount) {
        userCoinMapper.updateUserCoinsAmount(userId, amount);
    }
}
