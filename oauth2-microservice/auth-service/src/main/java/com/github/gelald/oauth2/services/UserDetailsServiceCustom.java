package com.github.gelald.oauth2.services;

import com.github.gelald.oauth2.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface UserDetailsServiceCustom extends UserDetailsService {
    UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException;
    UserDTO registry(String username, String password);
}
