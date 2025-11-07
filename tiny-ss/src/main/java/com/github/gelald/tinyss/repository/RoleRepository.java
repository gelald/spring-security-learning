package com.github.gelald.tinyss.repository;

import com.github.gelald.tinyss.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT DISTINCT r FROM Role r " +
           "JOIN r.userRoles ur " +
           "JOIN ur.user u " +
           "WHERE u.username = :username AND r.enabled = true AND ur.enabled = true")
    Set<Role> findRolesByUsername(@Param("username") String username);

    boolean existsByName(String name);

    List<Role> findByEnabledTrue();
}