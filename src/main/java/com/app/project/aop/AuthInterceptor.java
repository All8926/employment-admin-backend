package com.app.project.aop;

import com.app.project.annotation.AuthCheck;
import com.app.project.common.ErrorCode;
import com.app.project.exception.BusinessException;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.UserVO;
import com.app.project.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限校验 AOP
 *
 * @author 
 * @from 
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        String[] mustRoles = authCheck.mustRoles();

        // 统一整理所有需要的角色
        Set<String> requiredRoles = new HashSet<>();
        if (mustRole != null && !mustRole.trim().isEmpty()) {
            requiredRoles.add(mustRole.trim());
        }
        if (mustRoles != null && mustRoles.length > 0) {
            for (String role : mustRoles) {
                if (role != null && !role.trim().isEmpty()) {
                    requiredRoles.add(role.trim());
                }
            }
        }

        // 获取当前请求和用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        UserVO loginUser = userService.getLoginUser(request);

        // 获取用户角色
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null || UserRoleEnum.BAN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 如果没有指定任何角色，默认放行
        if (requiredRoles.isEmpty()) {
            return joinPoint.proceed();
        }

        // 检查是否有权限
        boolean hasPermission = requiredRoles.stream()
                .map(UserRoleEnum::getEnumByValue)
                .anyMatch(roleEnum -> roleEnum != null && roleEnum.equals(userRoleEnum));

        if (!hasPermission) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return joinPoint.proceed();

    }
}

