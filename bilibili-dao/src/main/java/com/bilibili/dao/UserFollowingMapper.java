package com.bilibili.dao;

import com.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Param;

public interface UserFollowingMapper {
    Integer unFollow(@Param("UserId") Long userId, @Param("followingId") Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);
}
