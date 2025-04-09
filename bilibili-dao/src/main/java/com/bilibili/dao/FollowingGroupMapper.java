package com.bilibili.dao;

import com.bilibili.domain.FollowingGroup;

public interface FollowingGroupMapper {
    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);
}
