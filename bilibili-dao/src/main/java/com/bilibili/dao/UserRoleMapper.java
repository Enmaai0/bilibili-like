package com.bilibili.dao;

import com.bilibili.domain.auth.UserRole;

import java.util.List;

public interface UserRoleMapper {
    List<UserRole> getUserRoleByUserId(Long userId);

    Integer addUserRole(UserRole userRole);
}
