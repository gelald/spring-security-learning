package com.github.gelald.oauth2.service;

import com.github.gelald.oauth2.entity.Role;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface RoleService {
    List<Role> findByUser(String userId);
}
