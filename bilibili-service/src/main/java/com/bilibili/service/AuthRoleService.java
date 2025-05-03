package com.bilibili.service;

import com.bilibili.domain.auth.AuthRoleElementOperation;
import com.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleService {
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIdSet(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationByRoleIdSet(roleIdSet);
    }

    public List<AuthRoleMenu> getRoleMenuByRoleIdSet(Set<Long> roleIdSet) {
        return authRoleMenuService.getRoleMenuByRoleIdSet(roleIdSet);
    }
}
