package com.github.gelald.tinyss.service;

import com.github.gelald.tinyss.entity.Role;
import com.github.gelald.tinyss.entity.Permission;
import com.github.gelald.tinyss.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
        }
        Role savedRole = roleRepository.save(role);
        log.info("Created role: {}", savedRole.getName());
        return savedRole;
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public List<Role> findAllEnabled() {
        return roleRepository.findByEnabledTrue();
    }

    public Set<Role> findRolesByUsername(String username) {
        return roleRepository.findRolesByUsername(username);
    }

    @Transactional
    public Role updateRole(Role role) {
        if (!roleRepository.existsById(role.getId())) {
            throw new IllegalArgumentException("Role with id " + role.getId() + " not found");
        }
        Role updatedRole = roleRepository.save(role);
        log.info("Updated role: {}", updatedRole.getName());
        return updatedRole;
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Role with id " + id + " not found");
        }
        roleRepository.deleteById(id);
        log.info("Deleted role with id: {}", id);
    }

    @Transactional
    public Role addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role with id " + roleId + " not found"));

        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission with id " + permissionId + " not found"));

        role.addPermission(permission);
        Role savedRole = roleRepository.save(role);
        log.info("Added permission {} to role {}", permission.getCode(), role.getName());
        return savedRole;
    }

    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role with id " + roleId + " not found"));

        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission with id " + permissionId + " not found"));

        role.removePermission(permission);
        Role savedRole = roleRepository.save(role);
        log.info("Removed permission {} from role {}", permission.getCode(), role.getName());
        return savedRole;
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}