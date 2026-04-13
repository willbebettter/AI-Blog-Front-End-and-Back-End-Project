# AI 博客系统 - 前端

> 基于 Vue 3 + Vite 构建的现代化博客平台前端

## 项目简介

这是一个使用 Vue 3 + Composition API 构建的响应式博客平台前端，提供流畅的用户体验和美观的暗色主题界面。

## 技术栈

| 技术 | 说明 |
|------|------|
| Vue 3 | 渐进式 JavaScript 框架 |
| Vue Router 4 | 官方路由管理 |
| Pinia | 轻量级状态管理 |
| Axios | HTTP 请求库 |
| Vite | 下一代前端构建工具 |

## 功能特性

- **用户系统**：注册、登录、个人资料管理
- **博客管理**：创建、编辑、删除博客文章
- **评论系统**：博客评论、博主置顶标识
- **社交功能**：关注/粉丝、点赞、收藏
- **搜索筛选**：按分类、标签、排序筛选博客
- **实时聊天**：博主与读者的私信功能
- **响应式设计**：适配各种屏幕尺寸

## 快速开始

### 环境要求

- Node.js >= 16.x
- npm >= 8.x

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
src/
├── api/               # API 接口封装
├── assets/            # 静态资源
├── components/        # 公共组件
│   ├── Header.vue     # 页头导航
│   ├── Footer.vue     # 页脚
│   ├── Modal.vue      # 模态框
│   └── SearchBox.vue  # 搜索框
├── layouts/           # 布局组件
├── router/            # 路由配置
├── store/             # Pinia 状态管理
├── utils/             # 工具函数
└── views/             # 页面组件
    ├── Home.vue           # 首页
    ├── Blog.vue           # 博客列表
    ├── BlogDetail.vue     # 博客详情
    ├── BlogCreate.vue     # 创建博客
    ├── BlogEdit.vue       # 编辑博客
    ├── AuthorProfile.vue  # 作者主页
    ├── UserCenter.vue     # 个人中心
    ├── ChatList.vue       # 聊天列表
    └── ChatDetail.vue     # 聊天详情
```

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | Home | 首页 |
| `/blog` | Blog | 博客列表 |
| `/blog/:id` | BlogDetail | 博客详情 |
| `/blog/create` | BlogCreate | 创建博客 |
| `/blog/edit/:id` | BlogEdit | 编辑博客 |
| `/author/:id` | AuthorProfile | 作者主页 |
| `/user-center` | UserCenter | 个人中心 |
| `/blog-management` | BlogManagement | 博客管理 |
| `/chat` | ChatList | 聊天列表 |
| `/chat/:id` | ChatDetail | 聊天详情 |
| `/login` | Login | 登录 |
| `/register` | Register | 注册 |

## 配置说明

如需修改后端 API 地址，编辑 `src/utils/api.js`：

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',  // 修改为你的后端地址
  timeout: 10000
})
```

## 与后端配合

本项目需要配合后端项目使用：

- **后端地址**：https://github.com/willbebettter/AI-Blog-Front-End-and-Back-End-Project

- **后端技术**：Spring Boot 3 + MySQL + JPA

- **数据库配置**：确保后端 MySQL 数据库已创建并运行

## 开发指南

### 添加新页面

1. 在 `src/views/` 创建 `.vue` 文件
2. 在 `src/router/index.js` 添加路由配置
3. 如需权限控制，在路由守卫中添加验证逻辑

### 添加 API 接口

1. 在 `src/api/` 目录下创建或编辑 API 模块
2. 使用 `api.js` 中封装的方法发送请求

### 样式规范

- 使用 Scoped CSS 避免样式冲突
- 颜色使用项目统一的暗色主题变量
- 动画效果保持简洁流畅

## License

MIT License
