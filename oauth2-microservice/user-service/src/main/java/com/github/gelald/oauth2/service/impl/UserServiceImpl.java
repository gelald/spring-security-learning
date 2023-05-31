package com.github.gelald.oauth2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.gelald.oauth2.dto.UserDTO;
import com.github.gelald.oauth2.entity.Role;
import com.github.gelald.oauth2.entity.User;
import com.github.gelald.oauth2.repository.UserRepository;
import com.github.gelald.oauth2.repository.RoleRepository;
import com.github.gelald.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDTO loadByUsername(String username) {
        Optional<User> optional = this.userRepository.findFirstByUsername(username);
        if (optional.isPresent()) {
            User User = optional.get();
            List<Role> roleList = this.roleRepository.findByUser(User.getId());
            return transfer(User, roleList);
        }
        return null;
    }

    @Override
    public UserDTO registry(String username, String password) {
        User User = new User();
        User.setUsername(username);
        User.setPassword(password);
        return transfer(User);
    }

    private UserDTO transfer(User User) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(User.getId());
        userDTO.setPassword(User.getPassword());
        userDTO.setUsername(User.getUsername());
        userDTO.setMobile(User.getMobileNumber());
        userDTO.setStatus(User.getStatus());
        return userDTO;
    }

    private UserDTO transfer(User User, List<Role> roleList) {
        UserDTO userDTO = transfer(User);
        if (CollUtil.isNotEmpty(roleList)) {
            userDTO.setRoles(roleList.stream().map(item -> item.getId() + "_" + item.getName()).collect(Collectors.toList()));
        }
        return userDTO;
    }
}
