package com.github.gelald.oauth2.repository;

import com.github.gelald.oauth2.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface AccountRoleRepository extends JpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole> {

}
