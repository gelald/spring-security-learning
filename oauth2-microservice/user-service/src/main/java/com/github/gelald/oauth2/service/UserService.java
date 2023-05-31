package com.github.gelald.oauth2.service;

import com.github.gelald.oauth2.dto.UserDTO;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface UserService {
    UserDTO loadByUsername(String username);

    UserDTO registry(String username, String password);
}
