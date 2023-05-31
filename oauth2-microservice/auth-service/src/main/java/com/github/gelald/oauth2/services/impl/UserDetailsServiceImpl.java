package com.github.gelald.oauth2.services.impl;

import com.github.gelald.oauth2.constant.MessageConstant;
import com.github.gelald.oauth2.dto.UserDTO;
import com.github.gelald.oauth2.feign.UserFeignClient;
import com.github.gelald.oauth2.response.Result;
import com.github.gelald.oauth2.response.ResultEnum;
import com.github.gelald.oauth2.security.UserDetailsCustom;
import com.github.gelald.oauth2.services.UserDetailsServiceCustom;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsServiceCustom {

    private final UserFeignClient userFeignClient;

    public UserDetailsServiceImpl(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Result<UserDTO> result = this.userFeignClient.loadAccountByUsername(username);
        if (ResultEnum.FAILED.getCode().equals(result.getCode())) {
            throw new RuntimeException(MessageConstant.SERVICE_FETCH_ERROR);
        }
        UserDTO userDTO = result.getData();
        if (userDTO == null) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        UserDetails userDetails = new UserDetailsCustom(userDTO);
        afterLoadUser(userDetails);
        return userDetails;
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException {
        Result<UserDTO> result = this.userFeignClient.loadAccountByMobile(mobile);
        if (ResultEnum.FAILED.getCode().equals(result.getCode())) {
            throw new RuntimeException(MessageConstant.SERVICE_FETCH_ERROR);
        }
        UserDTO userDTO = result.getData();
        if (userDTO == null) {
            throw new UsernameNotFoundException(MessageConstant.MOBILE_NUMBER_ERROR);
        }
        UserDetails userDetails = new UserDetailsCustom(userDTO);
        afterLoadUser(userDetails);
        return userDetails;
    }

    @Override
    public UserDTO registry(String username, String password) {
        Result<UserDTO> result = this.userFeignClient.registry(username, password);
        if (ResultEnum.FAILED.getCode().equals(result.getCode())) {
            throw new RuntimeException(MessageConstant.SERVICE_REGISTRY_ERROR);
        }
        return result.getData();
    }

    private void afterLoadUser(UserDetails userDetails) {
        if (!userDetails.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
    }
}