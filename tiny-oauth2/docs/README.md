# Tiny-OAuth2 学习文档

## 项目概述

本项目是基于 Spring Authorization Server 实现的 OAuth2 微服务授权中心，用于学习 OAuth2 协议和企业级安全架构设计。

## 项目结构

```
tiny-oauth2/
├── tiny-oauth2-auth/     # Authorization Server (授权服务器)
├── tiny-oauth2-gateway/  # Gateway + Resource Server (网关 + 资源服务器)
└── tiny-oauth2-user/     # Protected Resource Service (受保护的资源服务)
```

## 学习路径

### 第一阶段：Authorization Server 核心实现

1. [基础配置配置](01-authorization-server-basic-config.md)
2. [JWT密钥管理](02-jwt-key-management.md)
3. [用户认证实现](03-user-authentication.md)
4. [客户端注册配置](04-client-registration.md)
5. [Token端点配置](05-token-endpoints.md)
6. [授权流程测试](06-testing-authorization-flow.md)

### 第二阶段：Resource Server 集成

1. [Gateway配置](07-gateway-configuration.md)
2. [User服务集成](08-user-service-integration.md)
3. [权限控制](09-authorization-control.md)

### 第三阶段：企业级功能

1. [Token吊销机制](10-token-revocation.md)
2. [服务间调用](11-service-to-service.md)
3. [第三方登录集成](12-third-party-login.md)

## 核心概念

### OAuth2 角色
- **Resource Owner**: 资源所有者（通常是用户）
- **Client**: 客户端应用
- **Authorization Server**: 授权服务器
- **Resource Server**: 资源服务器

### OAuth2 授权模式
- **Authorization Code**: 授权码模式（推荐）
- **Client Credentials**: 客户端凭证模式（服务间调用）
- **Resource Owner Password Credentials**: 密码模式（不推荐）
- **Refresh Token**: 刷新令牌

### JWT Token 结构
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "key-id"
  },
  "payload": {
    "sub": "user-id",
    "iss": "http://localhost:9000",
    "aud": "client-id",
    "exp": 1234567890,
    "scope": "read write",
    "authorities": ["ROLE_USER", "ROLE_ADMIN"]
  }
}
```

## 端口分配

| 服务 | 端口 | 描述 |
|------|------|------|
| Authorization Server | 9000 | 授权服务器 |
| Gateway | 9001 | API网关 |
| User Service | 9002 | 用户服务 |

## 快速开始

1. 启动 Authorization Server
```bash
cd tiny-oauth2-auth
mvn spring-boot:run
```

2. 启动 Gateway
```bash
cd tiny-oauth2-gateway
mvn spring-boot:run
```

3. 启动 User Service
```bash
cd tiny-oauth2-user
mvn spring-boot:run
```

## 参考资料

- [Spring Authorization Server 官方文档](https://spring.io/projects/spring-authorization-server)
- [OAuth 2.1 RFC](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1)
- [JWT RFC](https://tools.ietf.org/html/rfc7519)
- [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)