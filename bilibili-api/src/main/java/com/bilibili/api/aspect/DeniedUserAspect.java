package com.bilibili.api.aspect;

import com.bilibili.domain.annotation.DeniedUser;
import com.bilibili.domain.auth.UserRole;
import com.bilibili.service.UserRoleService;
import com.bilibili.service.exception.ConditionalException;
import com.bilibili.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class DeniedUserAspect {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.bilibili.domain.annotation.DeniedUser)")
    public void check() {
    }

    @Before("check() && @annotation(deniedUser)")
    public void doBefore(JoinPoint joinPoint, DeniedUser deniedUser) {
        String[] deniedRoles = deniedUser.value();
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> codeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        codeSet.retainAll(Set.of(deniedRoles));
        if (!codeSet.isEmpty()) {
            throw new ConditionalException("Access denied");
        }
    }
}
