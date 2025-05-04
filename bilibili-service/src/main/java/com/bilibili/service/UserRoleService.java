package com.bilibili.service;

import com.bilibili.dao.UserRoleMapper;
import com.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleMapper.getUserRoleByUserId(userId);
    }

    public void addUserRole(UserRole userRole) {
        userRoleMapper.addUserRole(userRole);
    }
}
