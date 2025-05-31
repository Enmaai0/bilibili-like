package com.bilibili.dao;

import com.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuMapper {
    List<AuthRoleMenu> getRoleMenuByRoleIdSet(@Param("roleIdSet") Set<Long> roleIdSet);
}
