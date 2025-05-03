package com.bilibili.service;

import com.bilibili.dao.AuthRoleElementOperationMapper;
import com.bilibili.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleElementOperationService {
    @Autowired
    private AuthRoleElementOperationMapper authRoleElementOperationMapper;
    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIdSet(Set<Long> roleIdSet) {
        return authRoleElementOperationMapper.getRoleElementOperationByRoleIdSet(roleIdSet);
    }
}
