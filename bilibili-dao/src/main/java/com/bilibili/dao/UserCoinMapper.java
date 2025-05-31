package com.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCoinMapper {
    Integer getUserCoinAmount(Long userId);

    void updateUserCoinsAmount(@Param("userId") Long userId, @Param("amount") Integer amount);
}
