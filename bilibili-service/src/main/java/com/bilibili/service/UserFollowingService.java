package com.bilibili.service;

import com.bilibili.dao.UserFollowingMapper;
import com.bilibili.domain.*;
import com.bilibili.domain.constant.UserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new IllegalArgumentException("Invalid group ID");
            }
        }
            Long followingId = userFollowing.getFollowingId();
            User followingUser = userService.getUserById(followingId);
            if(followingUser == null) {
                throw new IllegalArgumentException("User not found");
            }
            userFollowingMapper.unFollow(userFollowing.getUserId(), followingId);
            userFollowingMapper.addUserFollowing(userFollowing);

    }

    public List<FollowingGroup> getUserFollowings(Long userId) {
        List<UserFollowing> userFollowings = userFollowingMapper.getUserFollowings(userId);
        List<Long> userFollowingIds = userFollowings.stream().map(UserFollowing::getFollowingId).toList();

        List<UserInfo> userInfos = new ArrayList<>();
        if(!userFollowingIds.isEmpty()) {
            userInfos = userService.getUserInfoByUserIds(userFollowingIds);
        }
        for(UserFollowing userFollowing : userFollowings) {
            for(UserInfo userInfo : userInfos) {
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                    break;
                }
            }
        }

        List<FollowingGroup> groups = followingGroupService.getByUserId(userId);

        Map<Long, List<UserInfo>> groupedUserInfoMap = new HashMap<>();
        for(FollowingGroup followingGroup : groups) {
            groupedUserInfoMap.put(followingGroup.getId(), new ArrayList<>());
        }
        for(UserFollowing userFollowing : userFollowings) {
            Long groupId = userFollowing.getGroupId();
            if(userFollowing.getUserInfo() != null) {
                groupedUserInfoMap.get(groupId).add(userFollowing.getUserInfo());
            }
        }

        List<FollowingGroup> result = new ArrayList<>();
        FollowingGroup allFollowingGroup = new FollowingGroup();
        allFollowingGroup.setName(UserConstant.USERFOLLOWING_ALL_FOLLOWING);
        allFollowingGroup.setUserInfoList(userInfos);
        result.add(allFollowingGroup);

        for(FollowingGroup followingGroup : groups) {
            FollowingGroup group = new FollowingGroup();
            group.setName(followingGroup.getName());
            group.setUserInfoList(groupedUserInfoMap.get(followingGroup.getId()));
            result.add((group));
        }
        return result;
    }

    public List<UserFollowing> getFans(Long userId) {
        List<UserFollowing> fansList = userFollowingMapper.getFans(userId);
        List<Long> fansIdList = fansList.stream()
                .map(UserFollowing::getUserId)
                .toList();
        List<UserInfo> fansInfoList = new ArrayList<>();
        if(!fansIdList.isEmpty()) {
            fansInfoList = userService.getUserInfoByUserIds(fansIdList);
        }
        List<UserFollowing> followingList = userFollowingMapper.getUserFollowings(userId);
        for(UserFollowing fans : fansList) {
            for(UserInfo fansInfo : fansInfoList) {
                if(fans.getUserId().equals(fansInfo.getUserId())) {
                    fansInfo.setFollowed(false);
                    fans.setUserInfo(fansInfo);
                    break;
                }
            }
            for(UserFollowing userFollowing : followingList) {
                if(fans.getId().equals(userFollowing.getFollowingId())) {
                    fans.getUserInfo().setFollowed(true);
                    break;
                }
            }
        }
        return fansList;
    }

    public Long addUserFollowingGroup(FollowingGroup followingGroup) {
        followingGroup.setType(UserConstant.USERFOLLOWING_GROUP_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfos, Long userId) {
        List<UserFollowing> userFollowings = userFollowingMapper.getUserFollowings(userId);
        List<Long> followingIds = userFollowings.stream().map(UserFollowing::getFollowingId).toList();
        for(UserInfo userInfo : userInfos) {
            if(followingIds.contains(userInfo.getUserId())) {
                userInfo.setFollowed(true);
            } else {
                userInfo.setFollowed(false);
            }
        }
        return userInfos;
    }
}
