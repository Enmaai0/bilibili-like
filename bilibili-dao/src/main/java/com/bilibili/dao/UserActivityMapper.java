package com.bilibili.dao;

import com.bilibili.domain.UserActivity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserActivityMapper {
    Integer addUserActivity(UserActivity userActivity);

}
