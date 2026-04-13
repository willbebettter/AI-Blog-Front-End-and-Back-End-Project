# AI 博客系统

> 一个功能完善、高效且美观的 AI 技术博客平台

## 项目简介

这是一个基于 AI 技术构建的博客系统，包含前端和后端两部分。

## 项目结构

```
AI-Blog-Full/
├── ai前端/          # Vue 3 前端项目
├── ai后端/          # Spring Boot 后端项目
└── README.md        # 项目总览文档
```

## 技术栈

### 前端
- Vue 3 + Composition API
- Vue Router 4
- Pinia 状态管理
- Axios
- Vite

### 后端
- Spring Boot 3
- Spring Security
- Spring Data JPA
- MySQL 8
- JWT 认证

## 功能特性

- **用户管理**：注册、登录、信息修改、头像上传
- **博客系统**：创建、编辑、删除、分类、标签
- **评论功能**：嵌套评论、博主置顶
- **社交互动**：关注/粉丝、点赞、收藏
- **实时聊天**：博主与读者的私信功能
- **响应式设计**：适配各种屏幕尺寸

## 快速开始

### 后端启动

```bash
cd ai后端
# 配置数据库连接
# 运行项目
```

### 前端启动

```bash
cd ai前端
npm install
npm run dev
```

## License

MIT