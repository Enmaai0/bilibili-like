package com.bilibili.service;

import com.bilibili.dao.AuthRoleMenuMapper;
import com.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleMenuService {
    @Autowired
    private AuthRoleMenuMapper authRoleMenuMapper;

    public List<AuthRoleMenu> getRoleMenuByRoleIdSet(@Param("roleIdSet") Set<Long> roleIdSet) {
        return authRoleMenuMapper.getRoleMenuByRoleIdSet(roleIdSet);
    }
}
