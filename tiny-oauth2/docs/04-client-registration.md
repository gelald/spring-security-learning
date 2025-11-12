# 客户端注册配置

## 学习目标

理解OAuth2客户端注册的概念，掌握不同类型客户端的配置方法。

## OAuth2 客户端类型

### 1. 公共客户端 (Public Client)
- 无法安全保存客户端密钥
- 如：SPA、移动应用
- 必须使用PKCE

### 2. 机密客户端 (Confidential Client)
- 可以安全保存客户端密钥
- 如：Web应用、微服务
- 可以使用客户端密钥认证

## RegisteredClient 配置

### 1. 核心属性

```java
RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
    .clientId("client-id")                    // 客户端ID
    .clientSecret("client-secret")            // 客户端密钥（机密客户端）
    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
    .redirectUri("http://localhost:8080/login/oauth2/code/client")
    .scope("read")
    .scope("write")
    .clientSettings(ClientSettings.builder()...)
    .tokenSettings(TokenSettings.builder()...)
    .build();
```

### 2. 客户端认证方法

```java
// HTTP Basic认证
ClientAuthenticationMethod.CLIENT_SECRET_BASIC

// POST表单认证
ClientAuthenticationMethod.CLIENT_SECRET_POST

// 无认证（公共客户端）
ClientAuthenticationMethod.NONE
```

### 3. 授权类型

```java
// 授权码模式（推荐）
AuthorizationGrantType.AUTHORIZATION_CODE

// 客户端凭证模式（服务间调用）
AuthorizationGrantType.CLIENT_CREDENTIALS

// 刷新令牌
AuthorizationGrantType.REFRESH_TOKEN

// 设备码授权
AuthorizationGrantType.DEVICE_CODE

// JWT Bearer授权
AuthorizationGrantType.JWT_BEARER
```

## 实际配置示例

### 1. Web应用客户端

```java
@Bean
public RegisteredClient webAppClient() {
    return RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("web-app-client")
        .clientSecret("{bcrypt}" + passwordEncoder().encode("web-app-secret"))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri("http://localhost:8080/login/oauth2/code/web-app")
        .redirectUri("http://localhost:8080/authorized")
        .postLogoutRedirectUri("http://localhost:8080/logout")
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope(OidcScopes.EMAIL)
        .scope("read")
        .scope("write")
        .clientSettings(ClientSettings.builder()
            .requireAuthorizationConsent(true)
            .requireProofKey(true)  // 启用PKCE
            .build())
        .tokenSettings(TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofMinutes(30))
            .refreshTokenTimeToLive(Duration.ofHours(8))
            .reuseRefreshTokens(false)
            .build())
        .build();
}
```

### 2. 微服务客户端

```java
@Bean
public RegisteredClient microserviceClient() {
    return RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("order-service")
        .clientSecret("{bcrypt}" + passwordEncoder().encode("order-service-secret"))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope("order.read")
        .scope("order.write")
        .scope("inventory.read")
        .tokenSettings(TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofHours(1))
            .build())
        .build();
}
```

### 3. SPA/移动应用客户端

```java
@Bean
public RegisteredClient spaClient() {
    return RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("spa-client")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)  // 公共客户端
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri("http://localhost:3000/auth/callback")
        .redirectUri("myapp://auth/callback")  // 移动应用自定义协议
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope("read")
        .clientSettings(ClientSettings.builder()
            .requireAuthorizationConsent(false)
            .requireProofKey(true)  // PKCE必需
            .build())
        .tokenSettings(TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofMinutes(15))
            .refreshTokenTimeToLive(Duration.ofDays(30))
            .build())
        .build();
}
```

## 客户端设置 (ClientSettings)

### 1. 常用配置

```java
ClientSettings.builder()
    .requireAuthorizationConsent(true)     // 需要用户授权确认
    .requireProofKey(true)                // 启用PKCE
    .jwkSetUrl("https://client.com/jwks") // JWT Bearer客户端的JWKS端点
    .tokenEndpointAuthenticationSigningAlgorithm("RS256")  // JWT签名算法
    .build()
```

### 2. PKCE配置

PKCE (Proof Key for Code Exchange) 适用于公共客户端：

```java
ClientSettings.builder()
    .requireProofKey(true)  // 强制使用PKCE
    .build()
```

PKCE流程：
```
1. 客户端生成 code_verifier (随机字符串)
2. 客户端使用 code_verifier 生成 code_challenge
3. 授权请求携带 code_challenge
4. Token请求携带 code_verifier
5. 服务器验证 code_verifier 与 code_challenge 的对应关系
```

## Token设置 (TokenSettings)

### 1. 访问令牌配置

```java
TokenSettings.builder()
    .accessTokenTimeToLive(Duration.ofMinutes(30))     // 访问令牌有效期
    .refreshTokenTimeToLive(Duration.ofHours(8))       // 刷新令牌有效期
    .reuseRefreshTokens(false)                         // 不重用刷新令牌
    .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // JWT格式
    .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256) // ID Token签名算法
    .build()
```

### 2. 令牌格式

```java
// 自包含令牌（JWT）
OAuth2TokenFormat.SELF_CONTAINED

// 引用令牌（不透明令牌）
OAuth2TokenFormat.REFERENCE
```

### 3. 令牌轮换

```java
TokenSettings.builder()
    .reuseRefreshTokens(false)  // 每次刷新都生成新的刷新令牌
    .build()
```

## 企业级实现

### 1. 数据库存储客户端

```java
@Entity
public class RegisteredClient {
    private String id;
    private String clientId;
    private String clientSecret;
    private String clientAuthenticationMethod;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;
    // ... 其他字段
}
```

### 2. 自定义RegisteredClientRepository

```java
@Service
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    @Autowired
    private RegisteredClientJpaRepository clientRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        // 转换并保存到数据库
    }

    @Override
    public RegisteredClient findById(String id) {
        // 从数据库加载并转换
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        // 从数据库加载并转换
    }
}
```

### 3. 客户端管理界面

```java
@RestController
@RequestMapping("/admin/clients")
public class ClientManagementController {

    @PostMapping
    public RegisteredClient createClient(@RequestBody CreateClientRequest request) {
        // 创建新客户端
    }

    @GetMapping("/{clientId}")
    public RegisteredClient getClient(@PathVariable String clientId) {
        // 获取客户端信息
    }

    @PutMapping("/{clientId}")
    public RegisteredClient updateClient(@PathVariable String clientId, @RequestBody UpdateClientRequest request) {
        // 更新客户端配置
    }

    @DeleteMapping("/{clientId}")
    public void deleteClient(@PathVariable String clientId) {
        // 删除客户端
    }
}
```

## 验证方法

### 1. 检查客户端注册

```bash
# 获取客户端信息（需要认证）
curl -u gateway-client:gateway-secret \
  http://localhost:9000/oauth2/authorize?client_id=gateway-client

# 检查客户端元数据
curl http://localhost:9000/.well-known/oauth-authorization-server
```

### 2. 测试授权码流程

```bash
# 1. 用户授权
GET http://localhost:9000/oauth2/authorize?
    response_type=code&
    client_id=gateway-client&
    scope=read&
    redirect_uri=http://127.0.0.1:8080/authorized&
    state=random_state

# 2. 获取访问令牌
POST http://localhost:9000/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic Z2F0ZXdheS1jbGllbnQ6Z2F0ZXdheS1zZWNyZXQ=

grant_type=authorization_code&
code=auth_code&
redirect_uri=http://127.0.0.1:8080/authorized
```

### 3. 测试客户端凭证模式

```bash
POST http://localhost:9000/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic dXNlci1zZXJ2aWNlLWNsaWVudDp1c2VyLXNlcnZpY2Utc2VjcmV0

grant_type=client_credentials&
scope=user.read
```

## 安全注意事项

### 1. 客户端密钥管理

- 使用强密码
- 定期轮换密钥
- 安全存储密钥

### 2. 重定向URI验证

- 严格验证重定向URI
- 避免开放重定向漏洞
- 使用HTTPS

### 3. 作用域管理

- 最小权限原则
- 动态作用域控制
- 作用域访问审计

## 面试要点

1. **公共客户端和机密客户端的区别？**
   - 是否能安全保存客户端密钥
   - 认证方式不同
   - 适用场景不同

2. **PKCE的作用是什么？**
   - 防止授权码拦截攻击
   - 保护公共客户端
   - 增强授权码模式安全性

3. **如何管理客户端的生命周期？**
   - 客户端注册和注销
   - 密钥轮换
   - 权限动态调整

## 常见问题

1. **客户端认证失败**
   - 检查客户端ID和密钥
   - 确认认证方法

2. **重定向URI错误**
   - 检查URI配置
   - 确认协议和端口

3. **作用域不足**
   - 检查客户端作用域配置
   - 确认授权范围

## 下一步

完成客户端注册后，下一步是实现Token端点配置和测试完整的授权流程。