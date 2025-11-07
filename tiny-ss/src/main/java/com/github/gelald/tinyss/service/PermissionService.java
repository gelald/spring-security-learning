package com.github.gelald.tinyss.service;

import com.github.gelald.tinyss.entity.Permission;
import com.github.gelald.tinyss.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Permission createPermission(Permission permission) {
        if (permissionRepository.existsByCode(permission.getCode())) {
            throw new IllegalArgumentException("Permission with code " + permission.getCode() + " already exists");
        }
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Created permission: {}", savedPermission.getCode());
        return savedPermission;
    }

    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public Optional<Permission> findByCode(String code) {
        return permissionRepository.findByCode(code);
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public List<Permission> findAllEnabled() {
        return permissionRepository.findAllEnabledPermissions();
    }

    public List<Permission> findByResourceType(String resourceType) {
        return permissionRepository.findByResourceType(resourceType);
    }

    public Permission updatePermission(Permission permission) {
        if (!permissionRepository.existsById(permission.getId())) {
            throw new IllegalArgumentException("Permission with id " + permission.getId() + " not found");
        }
        Permission updatedPermission = permissionRepository.save(permission);
        log.info("Updated permission: {}", updatedPermission.getCode());
        return updatedPermission;
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Permission with id " + id + " not found");
        }
        permissionRepository.deleteById(id);
        log.info("Deleted permission with id: {}", id);
    }

    public Set<String> findPermissionCodesByUsername(String username) {
        return permissionRepository.findPermissionCodesByUsername(username);
    }

    public boolean existsByCode(String code) {
        return permissionRepository.existsByCode(code);
    }
}