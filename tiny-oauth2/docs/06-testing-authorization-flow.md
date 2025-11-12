# 授权流程测试

## 学习目标

掌握OAuth2授权流程的测试方法，验证Authorization Server的功能正确性。

## 测试环境准备

### 1. 服务启动顺序

```bash
# 1. 启动Authorization Server (端口9000)
cd tiny-oauth2-auth
mvn spring-boot:run

# 2. 启动Gateway (端口9001)
cd tiny-oauth2-gateway
mvn spring-boot:run

# 3. 启动User Service (端口9002)
cd tiny-oauth2-user
mvn spring-boot:run
```

### 2. 验证服务状态

```bash
# 检查Authorization Server
curl http://localhost:9000/.well-known/oauth-authorization-server

# 检查JWKS端点
curl http://localhost:9000/oauth2/jwks

# 检查Gateway健康状态
curl http://localhost:9001/actuator/health

# 检查User Service健康状态
curl http://localhost:9002/actuator/health
```

## 测试场景

### 场景1：授权码模式 (Authorization Code Flow)

#### 1.1 用户授权请求

```bash
# 构造授权URL
GET http://localhost:9000/oauth2/authorize?
    response_type=code&
    client_id=gateway-client&
    scope=openid profile read&
    state=random_state&
    redirect_uri=http://127.0.0.1:8080/authorized
```

**预期结果：**
1. 重定向到登录页面
2. 用户输入：user/password
3. 显示授权确认页面
4. 用户同意后重定向到：`http://127.0.0.1:8080/authorized?code=xxx&state=random_state`

#### 1.2 获取访问令牌

```bash
# 使用授权码换取访问令牌
POST http://localhost:9000/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic Z2F0ZXdheS1jbGllbnQ6Z2F0ZXdheS1zZWNyZXQ=

grant_type=authorization_code&
code=接收到的授权码&
redirect_uri=http://127.0.0.1:8080/authorized
```

**预期响应：**
```json
{
  "access_token": "eyJ...",
  "refresh_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 1800,
  "scope": "openid profile read"
}
```

#### 1.3 使用访问令牌

```bash
# 访问受保护资源
GET http://localhost:9001/user/profile
Authorization: Bearer access_token_value
```

**预期结果：** 返回用户信息

#### 1.4 刷新访问令牌

```bash
POST http://localhost:9000/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic Z2F0ZXdheS1jbGllbnQ6Z2F0ZXdheS1zZWNyZXQ=

grant_type=refresh_token&
refresh_token=refresh_token_value
```

### 场景2：客户端凭证模式 (Client Credentials Flow)

#### 2.1 获取访问令牌

```bash
POST http://localhost:9000/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic dXNlci1zZXJ2aWNlLWNsaWVudDp1c2VyLXNlcnZpY2Utc2VjcmV0

grant_type=client_credentials&
scope=user.read
```

**预期响应：**
```json
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "user.read"
}
```

#### 2.2 服务间调用

```bash
# Gateway调用User Service
GET http://localhost:9001/user/info
Authorization: Bearer access_token_value
```

### 场景3：资源所有者密码凭证模式 (Resource Owner Password Credentials)

> 注意：此模式不推荐在生产环境使用，仅用于测试

```bash
POST http://localhost:9000/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic Z2F0ZXdheS1jbGllbnQ6Z2F0ZXdheS1zZWNyZXQ=

grant_type=password&
username=user&
password=password&
scope=read
```

## 令牌验证

### 1. JWT解码

```bash
# 解码JWT
# 安装jwt工具：npm install -g jwt-cli
# 或者使用在线工具：https://jwt.io/

jwt decode access_token_value
```

**预期内容：**
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "key-id"
  },
  "payload": {
    "sub": "user",
    "aud": "gateway-client",
    "iss": "http://localhost:9000",
    "exp": 1640995200,
    "iat": 1640991600,
    "scope": "read",
    "authorities": ["ROLE_USER"]
  }
}
```

### 2. 令牌有效性验证

```bash
# 验证令牌是否有效
GET http://localhost:9001/user/profile
Authorization: Bearer access_token_value

# 测试过期令牌
# 等待令牌过期后再次请求，应该返回401
```

### 3. JWKS验证

```bash
# 获取公钥
curl http://localhost:9000/oauth2/jwks

# 使用公钥验证JWT签名
# 可以使用在线JWT验证工具
```

## 错误场景测试

### 1. 无效客户端

```bash
POST http://localhost:9000/oauth2/token
Authorization: Basic invalid_client:invalid_secret

# 预期返回：401 Unauthorized
# {"error":"invalid_client"}
```

### 2. 无效授权码

```bash
POST http://localhost:9000/oauth2/token
Authorization: Basic Z2F0ZXdheS1jbGllbnQ6Z2F0ZXdheS1zZWNyZXQ=
grant_type=authorization_code&
code=invalid_code&
redirect_uri=http://127.0.0.1:8080/authorized

# 预期返回：400 Bad Request
# {"error":"invalid_grant"}
```

### 3. 无效令牌

```bash
GET http://localhost:9001/user/profile
Authorization: Bearer invalid_token

# 预期返回：401 Unauthorized
```

### 4. 权限不足

```bash
# 使用只有read权限的令牌访问需要write权限的资源
GET http://localhost:9001/user/write
Authorization: Bearer read_only_token

# 预期返回：403 Forbidden
```

## 性能测试

### 1. 并发授权请求

```bash
# 使用Apache Bench测试
ab -n 100 -c 10 "http://localhost:9000/oauth2/authorize?response_type=code&client_id=gateway-client&scope=read&redirect_uri=http://127.0.0.1:8080/authorized"

# 使用JMeter创建测试计划
# 模拟多用户并发获取令牌
```

### 2. 令牌验证性能

```bash
# 测试Resource Server的令牌验证性能
ab -n 1000 -c 50 -H "Authorization: Bearer valid_token" "http://localhost:9001/user/profile"
```

## 安全测试

### 1. 重定向攻击测试

```bash
# 尝试使用恶意重定向URI
GET http://localhost:9000/oauth2/authorize?
    response_type=code&
    client_id=gateway-client&
    scope=read&
    redirect_uri=http://evil.com/callback

# 预期返回：400 Bad Request
# {"error":"invalid_redirect_uri"}
```

### 2. 令牌泄露测试

```bash
# 测试令牌在日志中的暴露情况
# 检查是否记录了敏感信息
tail -f logs/application.log
```

### 3. 跨站请求伪造测试

```bash
# 测试CSRF保护
# 检查表单中是否包含CSRF token
curl -v http://localhost:9000/login
```

## 自动化测试

### 1. Postman集合

创建Postman测试集合，包含以下请求：

- Authorization Server端点测试
- Token获取和刷新
- 受保护资源访问
- 错误场景验证

### 2. JUnit测试

```java
@SpringBootTest
@AutoConfigureMockMvc
class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAuthorizationCodeFlow() {
        // 测试授权码流程
    }

    @Test
    void testClientCredentialsFlow() {
        // 测试客户端凭证流程
    }

    @Test
    void testTokenValidation() {
        // 测试令牌验证
    }
}
```

### 3. TestContainers

```java
@SpringBootTest
@Testcontainers
class OAuth2ContainerTest {

    @Container
    static GenericContainer<?> authServer = new GenericContainer<>("authorization-server:latest")
        .withExposedPorts(9000);

    @Test
    void testOAuth2FlowInContainer() {
        // 在容器中测试OAuth2流程
    }
}
```

## 监控和日志

### 1. 启用详细日志

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
    org.springframework.security.web: DEBUG
```

### 2. 监控指标

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### 3. 审计日志

```java
@Configuration
@EnableWebSecurity
public class AuditConfig {

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}
```

## 故障排查

### 1. 常见问题

- **401 Unauthorized**: 检查客户端认证
- **400 Bad Request**: 检查请求参数
- **403 Forbidden**: 检查权限配置
- **500 Internal Server Error**: 检查服务器日志

### 2. 调试技巧

- 启用DEBUG日志级别
- 检查JWT内容
- 验证客户端注册信息
- 确认重定向URI配置

### 3. 网络问题

- 检查防火墙设置
- 验证SSL证书
- 确认域名解析

## 面试要点

1. **如何测试OAuth2授权流程？**
   - 分步测试各个端点
   - 验证令牌格式和内容
   - 测试错误场景

2. **如何进行安全测试？**
   - 重定向攻击测试
   - 令牌泄露检查
   - CSRF保护验证

3. **性能测试关注点？**
   - 并发令牌获取
   - 令牌验证性能
   - 内存和CPU使用情况

## 常见问题

1. **授权码无效**
   - 检查授权码有效期
   - 确认重定向URI匹配

2. **令牌验证失败**
   - 检查JWT签名
   - 确认issuer和audience

3. **跨域问题**
   - 配置CORS
   - 检查预检请求

## 下一步

完成测试后，下一步是集成Gateway和User Service，实现完整的微服务安全架构。