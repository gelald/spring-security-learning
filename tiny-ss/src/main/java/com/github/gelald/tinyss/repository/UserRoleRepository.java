package com.github.gelald.tinyss.repository;

import com.github.gelald.tinyss.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur " +
           "JOIN FETCH ur.role r " +
           "JOIN FETCH ur.user u " +
           "WHERE u.id = :userId AND ur.enabled = true")
    List<UserRole> findByUserIdAndEnabledTrue(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserRole ur " +
           "JOIN FETCH ur.role r " +
           "JOIN FETCH ur.user u " +
           "WHERE u.username = :username AND ur.enabled = true")
    List<UserRole> findByUsernameAndEnabledTrue(@Param("username") String username);

    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}