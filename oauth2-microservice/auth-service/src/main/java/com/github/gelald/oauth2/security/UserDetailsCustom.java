package com.github.gelald.oauth2.security;

import com.github.gelald.oauth2.dto.UserDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
public class UserDetailsCustom implements UserDetails {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private Integer status;
    /**
     * 权限数据
     */
    private Collection<GrantedAuthority> authorities;

    public UserDetailsCustom(UserDTO userDTO) {
        this.id = userDTO.getId();
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.mobile = userDTO.getMobile();
        this.status = userDTO.getStatus();
        this.authorities = userDTO.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}