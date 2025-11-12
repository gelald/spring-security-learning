# 用户认证实现

## 学习目标

理解OAuth2中用户认证的流程，掌握UserDetailsService的实现方式。

## OAuth2 用户认证流程

### 1. 授权码模式流程

```
1. 用户访问客户端应用
2. 客户端重定向到授权服务器: /oauth2/authorize
3. 用户登录（使用UserDetailsService验证）
4. 授权服务器显示授权确认页面
5. 用户同意授权
6. 授权服务器重定向回客户端，携带授权码
7. 客户端使用授权码换取访问令牌
```

### 2. 用户认证 vs 客户端认证

- **用户认证**：验证Resource Owner的身份
- **客户端认证**：验证Client应用的身份

## UserDetailsService 详解

### 1. 接口定义

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

### 2. UserDetails 接口

```java
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
    String getPassword();
    String getUsername();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

### 3. GrantedAuthority

表示用户的权限/角色：

```java
// 角色
ROLE_USER, ROLE_ADMIN

// 权限
user:read, user:write, order:create
```

## 实现方式

### 1. 内存存储（当前实现）

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user")
        .password("password")
        .roles("USER")
        .build();

    UserDetails admin = User.withUsername("admin")
        .password("admin")
        .roles("USER", "ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user, admin);
}
```

### 2. 数据库存储（推荐）

```java
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRoles().toArray(new String[0]))
            .build();
    }
}
```

### 3. LDAP存储

```java
@Bean
public UserDetailsService userDetailsService() {
    return new LdapUserDetailsService(contextSource(), userSearch());
}
```

## 密码编码

### 1. 为什么需要密码编码？

- 明文存储密码不安全
- 防止彩虹表攻击
- 遵循安全最佳实践

### 2. BCryptPasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// 使用示例
String encodedPassword = passwordEncoder.encode("rawPassword");
boolean matches = passwordEncoder.matches("rawPassword", encodedPassword);
```

### 3. 其他编码器

```java
// PBKDF2（推荐）
new Pbkdf2PasswordEncoder();

// SCrypt
new SCryptPasswordEncoder();

// Argon2（最安全）
new Argon2PasswordEncoder();
```

## 企业级实现

### 1. 用户实体设计

```java
@Entity
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    @ManyToMany
    private Set<Role> roles;
}
```

### 2. 角色实体设计

```java
@Entity
public class Role {
    private Long id;
    private String name;  // ROLE_USER, ROLE_ADMIN
    private String description;

    @ManyToMany
    private Set<Permission> permissions;
}
```

### 3. 权限实体设计

```java
@Entity
public class Permission {
    private Long id;
    private String name;  // user:read, user:write
    private String description;
}
```

### 4. 动态权限加载

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 收集用户的所有权限
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 添加角色权限
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            // 添加具体权限
            role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        });

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(!user.isAccountNonExpired())
            .accountLocked(!user.isAccountNonLocked())
            .credentialsExpired(!user.isCredentialsNonExpired())
            .disabled(!user.isEnabled())
            .build();
    }
}
```

## 验证方法

### 1. 测试用户登录

```bash
# 使用curl测试
curl -X POST http://localhost:9000/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user&password=password"

# 或者访问浏览器进行表单登录
http://localhost:9000/oauth2/authorize?response_type=code&client_id=gateway-client&scope=read&redirect_uri=http://127.0.0.1:8080/authorized
```

### 2. 检查用户信息

```java
@RestController
public class UserController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return Map.of(
            "username", user.getUsername(),
            "authorities", user.getAuthorities()
        );
    }
}
```

## 安全注意事项

### 1. 密码策略

- 最小长度要求
- 复杂度要求
- 定期更换

### 2. 账户安全

- 登录失败锁定
- 密码重置流程
- 多因素认证

### 3. 会话管理

- 会话超时
- 并发登录限制
- 主动登出

## 面试要点

1. **UserDetailsService的作用是什么？**
   - 从数据源加载用户信息
   - 连接用户认证和业务数据
   - 支持多种用户存储方式

2. **为什么推荐使用BCryptPasswordEncoder？**
   - 自适应哈希函数
   - 内置盐值
   - 抗彩虹表攻击

3. **如何实现动态权限？**
   - 设计RBAC数据模型
   - 自定义UserDetailsService
   - 将权限转换为GrantedAuthority

## 常见问题

1. **UsernameNotFoundException**
   - 检查用户名是否正确
   - 确认数据源连接

2. **密码验证失败**
   - 检查密码编码器
   - 确认密码存储格式

3. **权限不足**
   - 检查角色配置
   - 确认权限映射

## 下一步

完成用户认证后，下一步是配置客户端注册和授权流程。