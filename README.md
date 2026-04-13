# AI 博客系统

## ✨ 项目简介

> 一个功能完善、高效且美观的 AI 技术博客平台

AI 博客系统是一个专为技术爱好者和内容创作者打造的现代化博客平台，集成了 AI 技术，提供丰富的社交互动功能。系统采用前后端分离架构，前端使用 Vue 3 构建，后端基于 Spring Boot 开发，确保了系统的高性能和可扩展性。

### 🛠 技术栈

**前端**
- Vue 3 + Composition API
- Vue Router 4
- Pinia 状态管理
- Axios
- Vite

**后端**
- Spring Boot 3
- Spring Security
- Spring Data JPA
- MySQL 8
- JWT 认证

## 📁 项目结构

```
AI-Blog-Full/
├── ai前端/          # Vue 3 前端项目
├── ai后端/          # Spring Boot 后端项目
└── README.md        # 项目总览文档
```

## 🚀 核心功能

### 用户系统
- 🔐 注册、登录、密码找回
- 📝 个人资料编辑、头像上传
- 👤 个人中心、关注/粉丝管理

### 博客管理
- ✏️ 创建、编辑、删除博客
- 📁 博客分类、标签管理
- 🖼️ 封面图片上传、富文本编辑
- 📊 博客统计、阅读量分析

### 社交互动
- ❤️ 博客点赞、收藏
- 💬 嵌套评论、回复功能
- 🔔 评论通知、系统通知
- 💌 博主与读者实时聊天

### 其他功能
- 🔍 全局搜索功能
- 📱 响应式设计，适配各种设备
- 📈 数据可视化统计
- 🔧 个人设置、系统配置

## 🔧 快速开始

### 环境要求

- **前端**：Node.js 16+，npm 7+
- **后端**：JDK 17+，Maven 3.8+，MySQL 8+

### 后端启动

1. **配置数据库**
   - 启动 MySQL 服务
   - 创建数据库 `blog_db`
   - 执行 `ai后端/src/main/resources/init.sql` 初始化数据库结构和示例数据

2. **修改配置文件**
   - 编辑 `ai后端/src/main/resources/application.yml`，配置数据库连接信息

3. **启动后端服务**
   ```bash
   cd ai后端
   mvn clean install
   mvn spring-boot:run
   ```
   后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. **安装依赖**
   ```bash
   cd ai前端
   npm install
   ```

2. **启动开发服务器**
   ```bash
   npm run dev
   ```
   前端服务将在 `http://localhost:3000` 启动

## 📖 使用指南

### 注册与登录
1. 访问 `http://localhost:3000`
2. 点击右上角「注册」按钮，填写注册信息
3. 注册成功后，使用账号密码登录系统

### 创建博客
1. 登录后，点击导航栏「写博客」按钮
2. 填写博客标题、内容、选择分类和标签
3. 点击「发布」按钮，完成博客创建

### 社交互动
1. 浏览博客时，可以点击「点赞」和「收藏」按钮
2. 在评论区发表评论，支持嵌套回复
3. 点击用户头像，进入用户主页，可选择「关注」该用户
4. 在「消息」页面查看系统通知和聊天消息

## 🤝 贡献指南

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证

## 🙏 致谢

- Vue 3 团队 - 提供优秀的前端框架
- Spring Boot 团队 - 提供强大的后端框架
- 所有开源贡献者

---

✨ 感谢使用 AI 博客系统！✨
如有问题或建议，欢迎提交 Issue 或 Pull Request
