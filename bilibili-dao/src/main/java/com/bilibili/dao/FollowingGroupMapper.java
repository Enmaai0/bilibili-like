package com.bilibili.dao;

import com.bilibili.domain.FollowingGroup;

import java.util.List;

public interface FollowingGroupMapper {
    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);

    Integer addFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
