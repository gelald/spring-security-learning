package com.github.gelald.oauth2.repository;

import com.github.gelald.oauth2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findFirstByUsername(String username);
}
