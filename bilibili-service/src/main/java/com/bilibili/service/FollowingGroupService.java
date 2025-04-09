package com.bilibili.service;

import com.bilibili.dao.FollowingGroupMapper;
import com.bilibili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowingGroupService {
    @Autowired
    private FollowingGroupMapper followingGroupMapper;

    public FollowingGroup getByType(String type) {
        return followingGroupMapper.getByType(type);
    }

    public FollowingGroup getById(Long id) {
        return followingGroupMapper.getById(id);
    }
}
