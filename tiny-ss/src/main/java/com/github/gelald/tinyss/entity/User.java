package com.github.gelald.tinyss.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"userRoles"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new java.util.HashSet<>();

    // 保留原有的简单角色字段用于向后兼容
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_simple_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();

        // 添加简单角色权限（向后兼容）
        if (roles != null && !roles.isEmpty()) {
            roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .forEach(authorities::add);
        }

        // 添加基于RBAC的权限
        if (userRoles != null && !userRoles.isEmpty()) {
            // 添加角色权限
            userRoles.stream()
                    .filter(ur -> ur.getEnabled() && ur.getRole().getEnabled())
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName()))
                    .forEach(authorities::add);

            // 添加具体权限
            userRoles.stream()
                    .filter(ur -> ur.getEnabled() && ur.getRole().getEnabled())
                    .flatMap(ur -> ur.getRole().getPermissions().stream())
                    .filter(Permission::getEnabled)
                    .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                    .forEach(authorities::add);
        }

        // 确保至少有一个基础角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}