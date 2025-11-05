# Spring Security 学习项目

## 项目概述

这是一个专注于Spring Security实现的综合学习项目，涵盖了多种认证和授权模式。项目结构为Maven多模块项目，包含三个主要学习领域：

1. **CAS学习** (`cas-learning`) - 演示CAS（中央认证服务）与Spring Security的集成
2. **OAuth2单体应用** (`oauth2-single`) - 展示在单体应用中的OAuth2实现
3. **OAuth2微服务** (`oauth2-microservice`) - 演示在分布式微服务架构中的OAuth2实现

## 项目结构

```
spring-security-learning/
├── cas-learning/           # CAS 认证示例
│   ├── cas-login-service/  # CAS 登录服务
│   ├── cas-biz-service/    # CAS 业务服务
│   └── cas-common/         # CAS 公共工具类
├── oauth2-single/          # OAuth2 单体应用
└── oauth2-microservice/    # OAuth2 微服务
    ├── auth-service/       # 认证服务
    ├── user-service/       # 用户服务
    ├── gateway-service/    # API网关
    ├── oauth2-core/        # OAuth2核心工具类
    └── sql/                # 数据库脚本
```

## 使用的技术

- **Java 8** - 编程语言
- **Spring Boot** - 应用程序框架
- **Spring Security** - 安全框架
- **OAuth2** - 授权框架
- **CAS (中央认证服务)** - 单点登录协议
- **Spring Cloud** - 微服务框架（用于分布式示例）
- **Maven** - 构建工具

## 构建和运行

### 前提条件
- Java 8+
- Maven 3.x

### 构建命令

构建整个项目:
```bash
mvn clean install
```

构建单独模块:
```bash
# 只构建OAuth2单体应用
mvn clean install -pl oauth2-single

# 只构建CAS学习模块
mvn clean install -pl cas-learning

# 只构建OAuth2微服务模块
mvn clean install -pl oauth2-microservice
```

### 运行应用程序

每个模块都包含可以单独运行的Spring Boot应用程序:

1. **OAuth2单体应用**:
   - 进入 `oauth2-single` 目录
   - 运行: `mvn spring-boot:run`

2. **OAuth2微服务**:
   - 每个服务（auth-service, user-service, gateway-service）都可以单独运行
   - 进入各个服务目录并运行: `mvn spring-boot:run`

3. **CAS学习**:
   - 登录服务和业务服务都需要运行才能演示CAS示例

## 模块详情

### CAS学习
演示CAS（中央认证服务）与Spring Security的集成，提供跨多个应用程序的单点登录功能。

- `cas-login-service`: 中央认证服务
- `cas-biz-service`: 使用CAS进行认证的业务服务
- `cas-common`: 共享的工具类和配置

### OAuth2单体应用
在单体应用上下文中展示OAuth2的实现，适用于单体应用程序。

### OAuth2微服务
在分布式微服务架构中OAuth2实现的综合示例：

- `auth-service`: 认证和授权服务
- `user-service`: 用户管理服务
- `gateway-service`: 用于路由和安全的API网关
- `oauth2-core`: 共享的OAuth2工具类
- `sql`: 数据库初始化脚本

## 开发规范

- Java 8 源码和目标兼容性
- 所有源文件使用UTF-8编码
- 标准Maven项目结构
- Spring Boot自动配置模式
- 以Spring Security为中心的安全优先方法
- RESTful API设计原则