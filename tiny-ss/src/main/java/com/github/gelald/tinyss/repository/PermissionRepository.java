package com.github.gelald.tinyss.repository;

import com.github.gelald.tinyss.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByResourceType(String resourceType);

    @Query("SELECT DISTINCT p.code FROM Permission p " +
           "JOIN p.roles r " +
           "JOIN r.userRoles ur " +
           "JOIN ur.user u " +
           "WHERE u.username = :username AND p.enabled = true AND r.enabled = true AND ur.enabled = true")
    Set<String> findPermissionCodesByUsername(@Param("username") String username);

    boolean existsByCode(String code);

    @Query("SELECT p FROM Permission p WHERE p.enabled = true ORDER BY p.resourceType, p.name")
    List<Permission> findAllEnabledPermissions();
}