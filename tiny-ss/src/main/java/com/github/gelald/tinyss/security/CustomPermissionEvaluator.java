package com.github.gelald.tinyss.security;

import com.github.gelald.tinyss.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Component("customPermissionEvaluator")
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionService permissionService;

    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        String permissionCode = permission.toString();

        log.debug("Checking permission {} for user {} on target {}", permissionCode, username, targetDomainObject);

        // 检查用户是否有特定权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(permissionCode));

        if (hasPermission) {
            log.debug("User {} has permission: {}", username, permissionCode);
            return true;
        }

        // 如果没有直接权限，检查动态权限
        Set<String> userPermissions = permissionService.findPermissionCodesByUsername(username);
        hasPermission = userPermissions.contains(permissionCode);

        log.debug("Permission check result for user {}: {} = {}", username, permissionCode, hasPermission);
        return hasPermission;
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        String permissionCode = permission.toString();

        log.debug("Checking permission {} for user {} on {} with id {}", permissionCode, username, targetType, targetId);

        // 基础权限检查
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(permissionCode));

        if (hasPermission) {
            return true;
        }

        // 动态权限检查
        Set<String> userPermissions = permissionService.findPermissionCodesByUsername(username);
        hasPermission = userPermissions.contains(permissionCode);

        // 对于特定资源的操作，可以添加额外的业务逻辑
        if (hasPermission && targetId != null) {
            // 例如：检查用户是否有权限访问特定资源
            hasPermission = checkResourceAccess(username, targetId, targetType, permissionCode);
        }

        log.debug("Permission check result for user {} on {} {}: {} = {}", username, targetType, targetId, permissionCode, hasPermission);
        return hasPermission;
    }

    private boolean checkResourceAccess(String username, Serializable targetId, String targetType, String permission) {
        // 这里可以实现更复杂的资源访问控制逻辑
        // 例如：检查用户是否是资源的所有者，或者是否有特定的业务权限

        switch (targetType.toLowerCase()) {
            case "user":
                return checkUserAccess(username, targetId, permission);
            case "order":
                return checkOrderAccess(username, targetId, permission);
            default:
                return true; // 默认允许访问
        }
    }

    private boolean checkUserAccess(String username, Serializable targetId, String permission) {
        // 示例：用户只能管理自己的信息，除非有管理员权限
        if (permission.equals("user:read") || permission.equals("user:update")) {
            // 如果目标用户就是当前用户，允许访问
            // 或者用户有管理员权限，允许访问
            return true;
        }
        return false;
    }

    private boolean checkOrderAccess(String username, Serializable targetId, String permission) {
        // 示例：订单访问控制逻辑
        // 用户只能访问自己的订单，除非有特殊权限
        return true;
    }
}