INSERT INTO users (username, password, email, nickname, bio, role, create_time, last_login_time) VALUES
('root', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'root@example.com', 'root', '系统管理员', 'ADMIN', NOW(), NOW()),
('zhangsan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'zhangsan@example.com', '张三', '技术专家，专注前端开发', 'USER', NOW(), NOW()),
('lisi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'lisi@example.com', '李四', '后端开发者，热爱分享技术', 'USER', NOW(), NOW()),
('wangwu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wangwu@example.com', '王五', '全栈工程师，喜欢探索新技术', 'USER', NOW(), NOW()),
('zhaoliu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'zhaoliu@example.com', '赵六', 'AI与机器学习爱好者，数据科学从业者', 'USER', NOW(), NOW()),
('sunqi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'sunqi@example.com', '孙七', 'UI/UX设计师，关注用户体验与交互设计', 'USER', NOW(), NOW()),
('zhouba', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'zhouba@example.com', '周八', '运维工程师，DevOps实践者，云原生布道师', 'USER', NOW(), NOW()),
('wujiu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wujiu@example.com', '吴九', '移动端开发，Flutter与React Native双修', 'USER', NOW(), NOW()),
('chenshi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'chenshi@example.com', '陈十', '产品经理转型开发者，关注效率工具与自动化', 'USER', NOW(), NOW())
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO categories (name, description, sort, create_time, update_time) VALUES
('技术', '技术相关文章', 1, NOW(), NOW()),
('生活', '生活感悟与分享', 2, NOW(), NOW()),
('编程', '编程技巧与实战', 3, NOW(), NOW()),
('设计', '设计理念与实践', 4, NOW(), NOW()),
('学习', '学习笔记与心得', 5, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO blog (title, content, summary, category, tags, author_id, views, likes, collects, status, create_time, update_time) VALUES
('Vue 3 新特性详解', '# Vue 3 新特性详解\n\n探索 Vue 3 的组合式 API、响应式系统和性能优化，包括 setup 函数、ref 和 reactive 的使用，以及如何迁移现有项目到 Vue 3。\n\n## 组合式 API\n\nVue 3 引入了组合式 API，让我们可以更灵活地组织组件逻辑...\n\n## 响应式系统\n\nVue 3 使用 Proxy 重写了响应式系统...', '探索 Vue 3 的组合式 API、响应式系统和性能优化，让你的前端开发更高效', '技术', 'tech,code,vue', 1, 1200, 88, 45, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
('Spring Boot 项目实战', '# Spring Boot 项目实战\n\n从0到1构建一个完整的 Spring Boot 应用，包含数据库、缓存和安全配置，以及如何部署到生产环境。\n\n## 项目初始化\n\n使用 Spring Initializr 创建项目...\n\n## 数据库配置\n\n配置 MySQL 数据源...', '从0到1构建一个完整的 Spring Boot 应用，包含数据库、缓存和安全配置', '技术', 'tech,code,spring', 2, 980, 66, 30, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
('前端性能优化技巧', '# 前端性能优化技巧\n\n提升前端应用性能的实用方法和最佳实践，包括代码分割、懒加载、缓存策略和网络优化。\n\n## 代码分割\n\n使用动态 import() 实现代码分割...\n\n## 懒加载\n\n图片懒加载和组件懒加载...', '提升前端应用性能的实用方法和最佳实践，让你的网站加载更快', '编程', 'tech,design,performance', 1, 850, 55, 25, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 26 DAY), NOW()),
('生活中的编程哲学', '# 生活中的编程哲学\n\n如何将编程思维应用到日常生活中，解决问题的思路和方法。\n\n## 分解问题\n\n编程教会我们将复杂问题分解为小问题...\n\n## 迭代优化\n\n先完成，再完美...', '如何将编程思维应用到日常生活中，解决问题的思路和方法', '生活', 'life,share,thinking', 1, 720, 44, 20, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
('Git 版本控制最佳实践', '# Git 版本控制最佳实践\n\n掌握 Git 的高级技巧和工作流，让代码管理更高效。\n\n## 分支策略\n\nGit Flow 和 Trunk Based Development...\n\n## 提交规范\n\nConventional Commits 规范...', '掌握 Git 的高级技巧和工作流，让代码管理更高效', '编程', 'code,tech,git', 2, 680, 33, 15, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 24 DAY), NOW()),
('Docker 容器化部署指南', '# Docker 容器化部署指南\n\n从零开始学习 Docker 和容器编排，简化应用部署和管理。\n\n## Dockerfile 编写\n\n最佳实践和常见陷阱...\n\n## Docker Compose\n\n多容器编排...', '从零开始学习 Docker 和容器编排，简化应用部署和管理', '技术', 'tech,code,docker', 2, 590, 22, 10, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 23 DAY), NOW()),
('TypeScript 高级类型体操', '# TypeScript 高级类型体操\n\n深入理解 TypeScript 的类型系统，掌握条件类型、映射类型和模板字面量类型等高级特性。\n\n## 条件类型\n\n条件类型让我们可以根据类型条件做推断...\n\n## 映射类型\n\n映射类型可以基于旧类型创建新类型...', '深入理解 TypeScript 的类型系统，掌握条件类型、映射类型等高级特性', '编程', 'tech,code,typescript', 1, 1100, 72, 38, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 22 DAY), NOW()),
('React Hooks 深度解析', '# React Hooks 深度解析\n\n从原理到实践，全面掌握 React Hooks 的使用技巧和常见陷阱。\n\n## useState 与 useReducer\n\n状态管理的两种方式...\n\n## useEffect 生命周期管理\n\n副作用处理的正确姿势...', '从原理到实践，全面掌握 React Hooks 的使用技巧和常见陷阱', '技术', 'tech,code,react', 4, 920, 58, 28, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 21 DAY), NOW()),
('Python 数据分析入门', '# Python 数据分析入门\n\n使用 Pandas 和 NumPy 进行数据分析的完整指南，从数据清洗到可视化展示。\n\n## Pandas 基础\n\nDataFrame 和 Series 的核心操作...\n\n## 数据清洗\n\n处理缺失值和异常值...', '使用 Pandas 和 NumPy 进行数据分析的完整指南', '编程', 'tech,code,python', 5, 780, 45, 22, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),
('机器学习模型调优实战', '# 机器学习模型调优实战\n\n从超参数搜索到模型融合，提升机器学习模型性能的实用技巧。\n\n## 网格搜索与随机搜索\n\n超参数调优的两种基本策略...\n\n## 交叉验证\n\n如何正确评估模型性能...', '从超参数搜索到模型融合，提升机器学习模型性能的实用技巧', '技术', 'tech,ai,machinelearning', 5, 650, 38, 18, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 19 DAY), NOW()),
('UI 设计规范与组件库搭建', '# UI 设计规范与组件库搭建\n\n如何建立统一的设计语言，并基于此搭建可复用的组件库。\n\n## 设计令牌\n\n颜色、字体、间距的统一管理...\n\n## 组件设计原则\n\n单一职责与可组合性...', '如何建立统一的设计语言，并基于此搭建可复用的组件库', '设计', 'design,ui,component', 6, 560, 42, 20, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
('Figma 高效设计工作流', '# Figma 高效设计工作流\n\n掌握 Figma 的高级功能，提升设计效率和团队协作能力。\n\n## Auto Layout\n\n自适应布局的奥秘...\n\n## 组件变体\n\n设计系统的核心...', '掌握 Figma 的高级功能，提升设计效率和团队协作能力', '设计', 'design,figma,workflow', 6, 480, 35, 16, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 17 DAY), NOW()),
('Kubernetes 集群运维指南', '# Kubernetes 集群运维指南\n\n生产环境 K8s 集群的部署、监控和故障排查全攻略。\n\n## 集群部署\n\nkubeadm 和 k3s 的选择...\n\n## 监控体系\n\nPrometheus + Grafana 搭建...', '生产环境 K8s 集群的部署、监控和故障排查全攻略', '技术', 'tech,devops,k8s', 7, 720, 40, 19, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 16 DAY), NOW()),
('CI/CD 流水线最佳实践', '# CI/CD 流水线最佳实践\n\n构建高效可靠的持续集成与持续部署流水线，加速软件交付。\n\n## Jenkins vs GitHub Actions\n\n主流 CI/CD 工具对比...\n\n## 流水线设计\n\n阶段划分与并行执行...', '构建高效可靠的持续集成与持续部署流水线，加速软件交付', '技术', 'tech,devops,cicd', 7, 610, 32, 14, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
('Flutter 跨平台开发实战', '# Flutter 跨平台开发实战\n\n使用 Flutter 构建高性能跨平台移动应用的完整指南。\n\n## Widget 体系\n\nStatelessWidget 与 StatelessWidget...\n\n## 状态管理\n\nProvider、Riverpod 与 Bloc...', '使用 Flutter 构建高性能跨平台移动应用的完整指南', '编程', 'tech,code,flutter', 8, 830, 50, 24, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
('React Native 与原生交互', '# React Native 与原生交互\n\n深入理解 React Native 的桥接机制，实现 JS 与原生代码的高效通信。\n\n## Native Modules\n\n如何编写原生模块...\n\n## Turbo Modules\n\n新架构下的模块开发...', '深入理解 React Native 的桥接机制，实现高效通信', '编程', 'tech,code,reactnative', 8, 570, 28, 12, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 13 DAY), NOW()),
('自动化脚本提升工作效率', '# 自动化脚本提升工作效率\n\n用 Python 和 Shell 脚本自动化日常重复任务，释放你的时间。\n\n## 文件批处理\n\n批量重命名和格式转换...\n\n## 定时任务\n\ncron 和 schedule 的使用...', '用 Python 和 Shell 脚本自动化日常重复任务，释放你的时间', '学习', 'tech,automation,python', 9, 490, 36, 17, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
('Notion 打造个人知识库', '# Notion 打造个人知识库\n\n利用 Notion 构建结构化的个人知识管理系统，让信息触手可及。\n\n## 数据库设计\n\n多维表格的灵活运用...\n\n## 模板系统\n\n复用与分享模板...', '利用 Notion 构建结构化的个人知识管理系统', '学习', 'tool,notion,productivity', 9, 520, 40, 21, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 11 DAY), NOW()),
('微服务架构设计模式', '# 微服务架构设计模式\n\n常见微服务架构模式及其适用场景，帮助你做出正确的架构决策。\n\n## 服务拆分策略\n\n按业务能力拆分 vs 按子域拆分...\n\n## 服务间通信\n\n同步与异步通信的选择...', '常见微服务架构模式及其适用场景，帮助你做出正确的架构决策', '技术', 'tech,architecture,microservice', 4, 890, 62, 35, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
('Node.js 后端开发指南', '# Node.js 后端开发指南\n\n使用 Express 和 Koa 构建 Node.js 后端服务的最佳实践。\n\n## Express vs Koa\n\n两大框架对比与选择...\n\n## 中间件机制\n\n洋葱模型与线性模型...', '使用 Express 和 Koa 构建 Node.js 后端服务的最佳实践', '编程', 'tech,code,nodejs', 4, 760, 48, 23, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),
('深度学习框架对比：PyTorch vs TensorFlow', '# 深度学习框架对比\n\n全面对比 PyTorch 和 TensorFlow 两大深度学习框架的优劣势。\n\n## 动态图 vs 静态图\n\n两种计算图模式的区别...\n\n## 生态系统\n\n工具链与社区支持对比...', '全面对比 PyTorch 和 TensorFlow 两大深度学习框架的优劣势', '技术', 'tech,ai,deeplearning', 5, 940, 56, 30, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
('CSS Grid 布局完全指南', '# CSS Grid 布局完全指南\n\n从基础到进阶，掌握 CSS Grid 的所有核心概念和实用技巧。\n\n## 网格容器与网格项\n\ngrid-template-columns 和 grid-template-rows...\n\n## 命名网格线\n\n使用命名网格线精确定位...', '从基础到进阶，掌握 CSS Grid 的所有核心概念和实用技巧', '编程', 'tech,code,css', 1, 670, 38, 16, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
('Redis 缓存策略详解', '# Redis 缓存策略详解\n\n深入理解 Redis 的缓存模式、过期策略和持久化机制。\n\n## 缓存模式\n\nCache Aside、Read Through、Write Through...\n\n## 过期策略\n\nLRU、LFU、FIFO 对比...', '深入理解 Redis 的缓存模式、过期策略和持久化机制', '技术', 'tech,code,redis', 2, 810, 52, 27, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
('设计系统从0到1', '# 设计系统从0到1\n\n如何从零开始构建一套完整的设计系统，提升产品一致性和开发效率。\n\n## 设计原则\n\n确定设计系统的核心价值观...\n\n## 组件规范\n\n按钮、输入框、卡片的设计规范...', '如何从零开始构建一套完整的设计系统，提升产品一致性', '设计', 'design,system,ui', 6, 630, 44, 22, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('Terraform 基础设施即代码', '# Terraform 基础设施即代码\n\n使用 Terraform 管理云资源，实现基础设施的版本控制和自动化部署。\n\n## HCL 语法\n\nTerraform 配置语言基础...\n\n## 状态管理\n\n远程状态与状态锁定...', '使用 Terraform 管理云资源，实现基础设施的版本控制', '技术', 'tech,devops,terraform', 7, 540, 30, 13, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
('移动端性能优化策略', '# 移动端性能优化策略\n\n针对 iOS 和 Android 平台的性能优化方案，包括启动优化、内存优化和渲染优化。\n\n## 启动优化\n\n冷启动与热启动的优化策略...\n\n## 内存管理\n\n内存泄漏检测与修复...', '针对 iOS 和 Android 平台的性能优化方案', '编程', 'tech,code,mobile', 8, 710, 46, 20, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
('效率工具推荐与使用技巧', '# 效率工具推荐与使用技巧\n\n精选10款提升开发效率的工具，以及它们的高级使用技巧。\n\n## 编辑器插件\n\nVS Code 必装插件推荐...\n\n## 命令行工具\n\noh-my-zsh 和 Starship...', '精选10款提升开发效率的工具，以及它们的高级使用技巧', '学习', 'tool,productivity,dev', 9, 430, 34, 15, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
('WebSocket 实时通信实战', '# WebSocket 实时通信实战\n\n使用 WebSocket 构建实时通信应用，包括聊天系统和实时数据推送。\n\n## WebSocket 协议\n\n握手过程与数据帧格式...\n\n## Spring WebSocket\n\nSTOMP 协议集成...', '使用 WebSocket 构建实时通信应用', '编程', 'tech,code,websocket', 4, 580, 35, 18, 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('自然语言处理入门指南', '# 自然语言处理入门指南\n\n从文本预处理到情感分析，NLP 核心概念和实战项目全解析。\n\n## 文本预处理\n\n分词、去停用词、词向量化...\n\n## 情感分析\n\n基于规则与基于深度学习的方法...', '从文本预处理到情感分析，NLP 核心概念和实战项目全解析', '技术', 'tech,ai,nlp', 5, 690, 42, 19, 'PUBLISHED', NOW(), NOW());

INSERT INTO notifications (user_id, from_user_id, type, title, content, is_read, create_time) VALUES
(1, NULL, 'SYSTEM', '欢迎来到AI博客平台', '欢迎注册AI博客平台，开始您的创作之旅！', false, NOW()),
(1, 2, 'FOLLOW', '新粉丝', '张三 关注了你', false, NOW()),
(1, 3, 'LIKE', '收到点赞', '李四 赞了你的文章《Vue 3 新特性详解》', true, DATE_SUB(NOW(), INTERVAL 1 HOUR));
