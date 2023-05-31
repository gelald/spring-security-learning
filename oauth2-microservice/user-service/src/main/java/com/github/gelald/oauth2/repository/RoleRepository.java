package com.github.gelald.oauth2.repository;

import com.github.gelald.oauth2.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    @Query(value = "select t1.* from oauth2_role t1" +
            " left join oauth2_user_role t2" +
            " on t1.id = t2.roleId" +
            " where t2.userId = :userId",
            nativeQuery = true)
    List<Role> findByUser(Long userId);
}
