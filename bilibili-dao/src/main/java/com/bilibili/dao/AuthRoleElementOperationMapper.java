package com.bilibili.dao;

import com.bilibili.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface AuthRoleElementOperationMapper {
    List<AuthRoleElementOperation> getRoleElementOperationByRoleIdSet(@Param("roleIdSet") Set<Long> roleIdSet);
}
