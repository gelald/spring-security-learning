package com.github.gelald.oauth2.service.impl;

import com.github.gelald.oauth2.entity.Role;
import com.github.gelald.oauth2.repository.RoleRepository;
import com.github.gelald.oauth2.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findByUser(String userId) {
        return null;
    }
}
