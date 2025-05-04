package com.bilibili.service;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.auth.AuthRoleElementOperation;
import com.bilibili.domain.auth.AuthRoleMenu;
import com.bilibili.domain.auth.UserAuthorities;
import com.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {
    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private UserRoleService userRoleService;

    public JsonResponse<UserAuthorities> getUserAuthorities(Long userId) {
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationByRoleIdSet(roleIdSet);
        List<AuthRoleMenu> roleMenuList = authRoleService.getRoleMenuByRoleIdSet(roleIdSet);

        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        userAuthorities.setRoleMenuList(roleMenuList);
        return JsonResponse.success(userAuthorities);
    }

    public void addUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(1L);
        userRole.setUserId(userId);
        userRoleService.addUserRole(userRole);
    }
}
