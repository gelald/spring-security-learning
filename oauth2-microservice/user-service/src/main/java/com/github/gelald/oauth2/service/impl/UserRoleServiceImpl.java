package com.github.gelald.oauth2.service.impl;

import com.github.gelald.oauth2.repository.AccountRoleRepository;
import com.github.gelald.oauth2.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private AccountRoleRepository accountRoleRepository;
}
