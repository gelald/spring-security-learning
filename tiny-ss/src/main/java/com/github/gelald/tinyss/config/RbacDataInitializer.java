package com.github.gelald.tinyss.config;

import com.github.gelald.tinyss.entity.*;
import com.github.gelald.tinyss.repository.UserRepository;
import com.github.gelald.tinyss.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(1) // 确保在 AdminUserInitializer 之前执行
@RequiredArgsConstructor
public class RbacDataInitializer implements CommandLineRunner {

    private final PermissionService permissionService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializePermissions();
        initializeRoles();
        assignPermissionsToRoles();
        createAdminUser();
        assignRolesToUsers();

        log.info("RBAC data initialization completed successfully!");
    }

    private void initializePermissions() {
        log.info("Initializing permissions...");

        List<Permission> permissions = Arrays.asList(
            // 用户管理权限
            new Permission("用户查看", "查看用户列表和详情", "user:read", "USER", "/admin/users"),
            new Permission("用户创建", "创建新用户", "user:create", "USER", "/admin/users/create"),
            new Permission("用户更新", "更新用户信息", "user:update", "USER", "/admin/users/update"),
            new Permission("用户删除", "删除用户", "user:delete", "USER", "/admin/users/delete"),

            // 角色管理权限
            new Permission("角色查看", "查看角色列表和详情", "role:read", "ROLE", "/admin/roles"),
            new Permission("角色创建", "创建新角色", "role:create", "ROLE", "/admin/roles/create"),
            new Permission("角色更新", "更新角色信息", "role:update", "ROLE", "/admin/roles/update"),
            new Permission("角色删除", "删除角色", "role:delete", "ROLE", "/admin/roles/delete"),

            // 权限管理权限
            new Permission("权限查看", "查看权限列表", "permission:read", "PERMISSION", "/admin/permissions"),
            new Permission("权限分配", "分配权限给角色", "permission:assign", "PERMISSION", "/admin/permissions/assign"),

            // 订单管理权限
            new Permission("订单查看", "查看订单列表", "order:read", "ORDER", "/admin/orders"),
            new Permission("订单创建", "创建新订单", "order:create", "ORDER", "/admin/orders/create"),
            new Permission("订单更新", "更新订单状态", "order:update", "ORDER", "/admin/orders/update"),
            new Permission("订单删除", "删除订单", "order:delete", "ORDER", "/admin/orders/delete"),

            // 系统管理权限
            new Permission("系统配置", "修改系统配置", "system:config", "SYSTEM", "/admin/system"),
            new Permission("日志查看", "查看系统日志", "log:read", "SYSTEM", "/admin/logs")
        );

        for (Permission permission : permissions) {
            if (!permissionService.existsByCode(permission.getCode())) {
                permissionService.createPermission(permission);
                log.info("Created permission: {}", permission.getCode());
            }
        }
    }

    private void initializeRoles() {
        log.info("Initializing roles...");

        List<Role> roles = Arrays.asList(
            new Role("ADMIN", "系统管理员"),
            new Role("USER_MANAGER", "用户管理员"),
            new Role("ORDER_MANAGER", "订单管理员"),
            new Role("USER", "普通用户")
        );

        for (Role role : roles) {
            if (!roleService.existsByName(role.getName())) {
                roleService.createRole(role);
                log.info("Created role: {}", role.getName());
            }
        }
    }

    private void assignPermissionsToRoles() {
        log.info("Assigning permissions to roles...");

        // ADMIN 角色 - 拥有所有权限
        Role adminRole = roleService.findByName("ADMIN").orElseThrow();
        assignAllPermissionsToRole(adminRole);

        // USER_MANAGER 角色 - 拥有用户管理权限
        Role userManagerRole = roleService.findByName("USER_MANAGER").orElseThrow();
        assignUserPermissionsToRole(userManagerRole);

        // ORDER_MANAGER 角色 - 拥有订单管理权限
        Role orderManagerRole = roleService.findByName("ORDER_MANAGER").orElseThrow();
        assignOrderPermissionsToRole(orderManagerRole);

        // USER 角色 - 只有基础权限
        Role userRole = roleService.findByName("USER").orElseThrow();
        assignBasicPermissionsToRole(userRole);
    }

    private void assignAllPermissionsToRole(Role role) {
        List<Permission> allPermissions = permissionService.findAll();
        for (Permission permission : allPermissions) {
            roleService.addPermissionToRole(role.getId(), permission.getId());
        }
        log.info("Assigned all permissions to role: {}", role.getName());
    }

    private void assignUserPermissionsToRole(Role role) {
        String[] userPermissionCodes = {
            "user:read", "user:create", "user:update", "user:delete",
            "role:read", "permission:read"
        };

        for (String permissionCode : userPermissionCodes) {
            permissionService.findByCode(permissionCode).ifPresent(permission -> {
                roleService.addPermissionToRole(role.getId(), permission.getId());
            });
        }
        log.info("Assigned user management permissions to role: {}", role.getName());
    }

    private void assignOrderPermissionsToRole(Role role) {
        String[] orderPermissionCodes = {
            "order:read", "order:create", "order:update", "order:delete"
        };

        for (String permissionCode : orderPermissionCodes) {
            permissionService.findByCode(permissionCode).ifPresent(permission -> {
                roleService.addPermissionToRole(role.getId(), permission.getId());
            });
        }
        log.info("Assigned order management permissions to role: {}", role.getName());
    }

    private void assignBasicPermissionsToRole(Role role) {
        String[] basicPermissionCodes = {
            "user:read", "order:read"
        };

        for (String permissionCode : basicPermissionCodes) {
            permissionService.findByCode(permissionCode).ifPresent(permission -> {
                roleService.addPermissionToRole(role.getId(), permission.getId());
            });
        }
        log.info("Assigned basic permissions to role: {}", role.getName());
    }

    private void createAdminUser() {
        log.info("Creating admin user...");

        // 检查 admin 用户是否已存在
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPassword(passwordEncoder.encode("admin123")); // 使用注入的编码器
            admin.setEnabled(true);

            userRepository.save(admin);
            log.info("Admin user created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }

    private void assignRolesToUsers() {
        log.info("Assigning roles to users...");

        // 给 admin 用户分配 ADMIN 角色
        userRepository.findByUsername("admin").ifPresent(adminUser -> {
            Role adminRole = roleService.findByName("ADMIN").orElseThrow();
            userRoleService.assignRoleToUser(adminUser, adminRole, "SYSTEM");
            log.info("Assigned ADMIN role to admin user");
        });
    }
}