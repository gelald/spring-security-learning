# Authorization Server 基础配置

## 学习目标

理解并实现 Spring Authorization Server 的基础配置，搭建一个可运行的授权服务器。

## 核心组件

### 1. SecurityFilterChain 配置

Authorization Server 需要两个关键的 SecurityFilterChain：

```java
@Configuration
public class SecurityConfig {

    // 授权服务器安全配置
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 配置授权服务器端点的安全规则
    }

    // 默认安全配置
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 配置其他请求的安全规则
    }
}
```

### 2. UserDetailsService

负责从数据源加载用户信息：

```java
@Bean
public UserDetailsService userDetailsService() {
    // 返回自定义的 UserDetailsService 实现
}
```

### 3. RegisteredClientRepository

管理OAuth2客户端注册信息：

```java
@Bean
public RegisteredClientRepository registeredClientRepository() {
    // 返回客户端注册仓库
}
```

### 4. AuthorizationService

核心授权服务：

```java
@Bean
public AuthorizationServerSettings authorizationServerSettings() {
    // 配置授权服务器设置
}
```

## 实现步骤

### 步骤1：创建 SecurityConfig 类

创建配置类，定义两个 SecurityFilterChain：

1. **Authorization Server SecurityFilterChain**: 保护授权服务器端点
2. **Default SecurityFilterChain**: 保护其他API端点

### 步骤2：配置端点访问规则

- `/oauth2/**` - 允许所有请求（用于授权流程）
- 其他端点需要认证

### 步骤3：实现 UserDetailsService

- 定义用户信息
- 配置密码编码器
- 设置用户权限

### 步骤4：配置客户端注册

- 定义客户端ID和密钥
- 设置支持的授权类型
- 配置重定向URI
- 设置权限范围

## 关键配置项

### 端点映射

- `/oauth2/authorize` - 授权端点
- `/oauth2/token` - Token端点
- `/oauth2/jwks` - JWKS端点
- `/.well-known/oauth-authorization-server` - 发现端点

### 安全规则

```java
.formLogin(withDefaults())  // 启用表单登录
.httpBasic(withDefaults()) // 启用HTTP Basic认证
```

## 验证方法

1. 访问 `http://localhost:9000/.well-known/oauth-authorization-server`
2. 查看授权服务器元数据
3. 访问 `http://localhost:9000/oauth2/jwks` 查看JWKS端点

## 面试要点

1. **为什么需要两个SecurityFilterChain？**
   - 授权服务器端点需要特殊的安全配置
   - 默认端点需要不同的认证方式

2. **UserDetailsService的作用是什么？**
   - 从数据源加载用户信息
   - 支持多种用户存储方式
   - 与Spring Security认证体系集成

3. **RegisteredClientRepository的作用？**
   - 管理OAuth2客户端信息
   - 支持内存和数据库存储
   - 控制客户端的权限和范围

## 常见问题

1. **端点404错误**
   - 检查SecurityFilterChain配置
   - 确认端点路径正确

2. **认证失败**
   - 检查UserDetailsService实现
   - 确认密码编码器配置

3. **客户端认证失败**
   - 检查客户端注册信息
   - 确认客户端密钥正确

## 下一步

完成基础配置后，下一步是配置JWT密钥管理，实现基于JWT的Token。