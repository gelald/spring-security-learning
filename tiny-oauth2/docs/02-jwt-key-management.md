# JWT密钥管理

## 学习目标

理解JWT签名密钥的生成和管理，掌握JWK和JWKS的概念及实现。

## 核心概念

### 1. JWT (JSON Web Token)

JWT是一种开放标准（RFC 7519），用于在各方之间安全地传输信息。

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "key-id"
  },
  "payload": {
    "iss": "http://localhost:9000",
    "sub": "user-id",
    "aud": "client-id",
    "exp": 1234567890,
    "iat": 1234567800,
    "scope": "read write"
  }
}
```

### 2. JWK (JSON Web Key)

用于表示加密密钥的JSON格式：

```json
{
  "kty": "RSA",
  "kid": "key-id",
  "use": "sig",
  "n": "public-key-modulus",
  "e": "AQAB",
  "d": "private-key-exponent"
}
```

### 3. JWKS (JSON Web Key Set)

JWK的集合，用于发布公钥：

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "key-id-1",
      "use": "sig",
      "n": "...",
      "e": "AQAB"
    }
  ]
}
```

## 实现步骤

### 步骤1：生成RSA密钥对

```java
private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
        throw new IllegalStateException(ex);
    }
    return keyPair;
}
```

### 步骤2：创建JWK源

```java
@Bean
public JWKSource<SecurityContext> jwkSource() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
}
```

### 步骤3：配置JWT解码器

```java
@Bean
public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
}
```

## 密钥管理策略

### 1. 静态密钥（当前实现）

- 使用固定的RSA密钥对
- 适合开发和小规模部署
- 密钥轮换困难

### 2. 密钥轮换

生产环境推荐实现密钥轮换：

```java
@Bean
public JWKSource<SecurityContext> jwkSource() {
    // 从文件或密钥存储加载密钥
    // 支持多个密钥和密钥轮换
}
```

### 3. 外部密钥存储

- Hashicorp Vault
- AWS KMS
- Azure Key Vault

## 安全注意事项

### 1. 密钥长度

- RSA密钥至少2048位
- 推荐使用3072位或4096位

### 2. 密钥存储

- 私钥必须安全存储
- 不要将私钥硬编码在代码中
- 使用密钥管理服务

### 3. 密钥轮换

- 定期轮换密钥
- 保持向前兼容性
- 监控密钥使用情况

## 验证方法

### 1. 检查JWKS端点

```bash
curl http://localhost:9000/oauth2/jwks
```

### 2. 解码JWT Token

```bash
# 获取token
token="your-jwt-token"

# 解码header
echo $token | cut -d. -f1 | base64 -d

# 解码payload
echo $token | cut -d. -f2 | base64 -d
```

### 3. 验证签名

使用公钥验证JWT签名是否有效。

## 面试要点

1. **为什么使用RSA签名而不是HMAC？**
   - 非对称加密，公钥可公开
   - 支持多方验证
   - 适合分布式系统

2. **JWKS端点的作用是什么？**
   - 发布验证JWT所需的公钥
   - 支持密钥轮换
   - 提供标准化的密钥发现机制

3. **如何安全地管理密钥？**
   - 使用专用的密钥管理服务
   - 实施密钥轮换策略
   - 控制密钥访问权限

## 常见问题

1. **密钥生成失败**
   - 检查JVM加密扩展
   - 确认密钥长度配置

2. **JWT验证失败**
   - 检查公钥私钥匹配
   - 确认算法配置正确

3. **性能问题**
   - 考虑缓存JWK
   - 优化密钥大小

## 下一步

完成JWT密钥配置后，下一步是实现用户认证和客户端注册。