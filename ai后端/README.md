# AI 博客系统 - 后端

> 基于 Spring Boot 3 + MySQL 构建的博客平台后端服务

## 项目简介

这是一个使用 Spring Boot 3 + JPA + MySQL 构建的博客平台后端服务，提供完整的 RESTful API，支持用户管理、博客 CRUD、评论、关注、收藏等功能。

## 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot 3.3.5 | 核心框架 |
| Spring Security | 安全认证 |
| Spring Data JPA | ORM 持久化 |
| MySQL 8 | 关系型数据库 |
| JWT | Token 认证 |
| Lombok | 简化代码 |
| Slf4j | 日志管理 |

## 功能特性

- **用户管理**：注册、登录、信息修改、头像上传
- **博客系统**：创建、编辑、删除、分类、标签
- **评论功能**：嵌套评论、博主置顶
- **社交互动**：关注/粉丝、点赞、收藏
- **数据统计**：浏览量、点赞数、收藏数
- **搜索功能**：关键词搜索、分类筛选、标签筛选、排序
- **分页支持**：博客列表、评论分页

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：

```sql
CREATE DATABASE blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
    username: your_username
    password: your_password
```

### 启动项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

或直接运行 `AiBackendApplication.java`

服务将在 http://localhost:8080/api 启动

## 项目结构

```
src/main/java/com/ai/backend/
├── AiBackendApplication.java    # 应用入口
├── common/                      # 公共类
│   ├── GlobalExceptionHandler.java  # 全局异常处理
│   └── Result.java              # 统一响应格式
├── config/                      # 配置类
│   ├── CorsConfig.java         # 跨域配置
│   └── SecurityConfig.java     # 安全配置
├── controller/                  # 控制器层
│   ├── BlogController.java      # 博客接口
│   ├── CommentController.java   # 评论接口
│   ├── UserController.java      # 用户接口
│   ├── FollowController.java    # 关注接口
│   └── NotificationController.java  # 通知接口
├── entity/                     # 实体类
│   ├── Blog.java               # 博客实体
│   ├── Comment.java            # 评论实体
│   ├── User.java               # 用户实体
│   ├── Follow.java             # 关注实体
│   ├── BlogLike.java           # 点赞实体
│   ├── BlogCollect.java        # 收藏实体
│   └── Notification.java       # 通知实体
└── mapper/                     # 数据访问层
    ├── BlogMapper.java         # 博客 Mapper
    ├── UserRepository.java     # 用户 Repository
    ├── CommentRepository.java  # 评论 Repository
    └── FollowRepository.java   # 关注 Repository
```

## API 接口

### 用户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user/register | 用户注册 |
| POST | /user/login | 用户登录 |
| GET | /user/{id} | 获取用户信息 |
| PUT | /user/{id} | 更新用户信息 |

### 博客接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /blog | 获取博客列表（分页） |
| GET | /blog/{id} | 获取博客详情 |
| POST | /blog | 创建博客 |
| PUT | /blog/{id} | 更新博客 |
| DELETE | /blog/{id} | 删除博客 |
| GET | /blog/search | 搜索博客 |
| GET | /blog/tag/{tag} | 按标签获取博客 |
| GET | /blog/popular | 获取热门博客 |
| POST | /blog/{id}/like | 点赞/取消点赞 |
| POST | /blog/{id}/collect | 收藏/取消收藏 |

### 评论接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /comment/blog/{blogId} | 获取博客评论 |
| POST | /comment | 添加评论 |
| DELETE | /comment/{id} | 删除评论 |

### 关注接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /follow | 关注用户 |
| DELETE | /follow/{id} | 取消关注 |
| GET | /follow/followers/{userId} | 获取粉丝列表 |
| GET | /follow/following/{userId} | 获取关注列表 |

## 数据库表结构

项目启动时会自动创建以下表：

- `users` - 用户表
- `blog` - 博客表
- `comments` - 评论表
- `follow` - 关注关系表
- `blog_like` - 点赞表
- `blog_collect` - 收藏表
- `blog_view` - 浏览记录表
- `notification` - 通知表

## 配置说明

主要配置项在 `src/main/resources/application.yml`：

```yaml
# 服务端口
server:
  port: 8080

# 数据库连接
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db
    username: root
    password: 123456

# CORS 配置（允许的前端地址）
cors:
  allowed-origins:
    - http://localhost:5173

# 日志级别
logging:
  level:
    com.ai.backend: DEBUG
```

## 与前端配合

本项目需要配合前端项目使用：

- **前端地址**：https://github.com/willbebettter/AI-Blog-Front-End-and-Back-End-Project

- **前端技术**：Vue 3 + Vite

- **前端端口**：5173（开发环境）

- **API 基础路径**：`http://localhost:8080/api`

## 开发指南

### 添加新接口

1. 在 `entity/` 创建新的实体类
2. 在 `mapper/` 创建对应的 Repository
3. 在 `controller/` 创建控制器
4. 使用 `@CrossOrigin` 注解处理跨域（如需要）
5. 返回统一的 `Result` 响应格式

### 接口响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

错误响应：

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

## License

MIT License
