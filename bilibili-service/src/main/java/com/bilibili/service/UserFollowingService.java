package com.bilibili.service;

import com.bilibili.dao.UserFollowingMapper;
import com.bilibili.domain.FollowingGroup;
import com.bilibili.domain.User;
import com.bilibili.domain.UserFollowing;
import com.bilibili.domain.constant.UserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFollowingService {
    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserFollowingMapper userFollowingMapper;

    public void addUserFollowing(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        if(groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USERFOLLOWING_GROUP_DEFAULT);
            userFollowing.setGroupId(followingGroup.getUserId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if(followingGroup == null) {
                throw new IllegalArgumentException("Invalid group ID");
            }
            Long followingId = userFollowing.getFollowingId();
            User followingUser = userService.getUserById(followingId);
            if(followingUser == null) {
                throw new IllegalArgumentException("User not found");
            }
            userFollowingMapper.unFollow(userFollowing.getUserId(), followingId);
            userFollowingMapper.addUserFollowing(userFollowing);
        }
    }
}
