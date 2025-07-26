# VideoHub | 视频分享平台

[English](#english) | [中文](#chinese)

---

## English

### About This Project

VideoHub is a modern video sharing platform built with Vue.js. It's designed to provide users with a seamless experience for watching, uploading, and sharing videos. The project demonstrates full-stack frontend development skills with a focus on user experience and modern web technologies.

### Key Features

**User Management**
- User registration and authentication
- Personal profile management
- Secure login with RSA encryption

**Video Platform**
- High-quality video playback using XGPlayer
- Video upload and management
- Chapter-based video organization
- Detailed video information pages

**Social Features**
- Post and share moments
- Follow/follower system
- Like and favorite functionality
- Advanced search capabilities

**Responsive Design**
- Mobile-friendly interface
- Waterfall layout for content discovery
- Modern and intuitive UI

### Tech Stack

- **Frontend Framework**: Vue.js 2.6.11
- **State Management**: Vuex 3.4.0
- **Routing**: Vue Router 3.2.0
- **UI Components**: Element UI 2.15.6
- **HTTP Client**: Axios 0.24.0
- **Video Player**: XGPlayer 2.31.4
- **Build Tool**: Vue CLI 4.5.0
- **Styling**: Sass/SCSS
- **Code Quality**: ESLint

### Project Structure

```
src/
├── components/     # Reusable components
├── views/         # Page components
├── router/        # Route configuration
├── store/         # Vuex state management
├── fetch/         # API services
└── assets/        # Static resources
```

### Getting Started

**Prerequisites**
- Node.js (version 12 or higher)
- npm or yarn

**Installation**
```bash
# Clone the repository
git clone [your-repo-url]

# Install dependencies
npm install

# Start development server
npm run serve

# Build for production
npm run build
```

### Development Notes

This project was built as part of my learning journey in frontend development. It showcases:
- Component-based architecture
- State management patterns
- API integration
- Responsive design principles
- Modern JavaScript (ES6+)

---

## Chinese

### 项目介绍

VideoHub 是一个基于 Vue.js 构建的现代化视频分享平台。这个项目为用户提供流畅的视频观看、上传和分享体验。

### 主要功能

**用户系统**
- 用户注册和身份验证
- 个人资料管理
- RSA 加密安全登录

**视频平台**
- 基于 XGPlayer 的高质量视频播放
- 视频上传和管理
- 章节化视频组织
- 详细的视频信息页面

**社交功能**
- 发布和分享动态
- 关注/粉丝系统
- 点赞和收藏功能
- 高级搜索功能

**响应式设计**
- 移动端友好界面
- 瀑布流内容布局
- 现代化直观的用户界面

### 技术栈

- **前端框架**: Vue.js 2.6.11
- **状态管理**: Vuex 3.4.0
- **路由管理**: Vue Router 3.2.0
- **UI 组件**: Element UI 2.15.6
- **HTTP 客户端**: Axios 0.24.0
- **视频播放器**: XGPlayer 2.31.4
- **构建工具**: Vue CLI 4.5.0
- **样式预处理**: Sass/SCSS
- **代码质量**: ESLint

### 项目结构

```
src/
├── components/     # 可复用组件
├── views/         # 页面组件
├── router/        # 路由配置
├── store/         # Vuex 状态管理
├── fetch/         # API 服务
└── assets/        # 静态资源
```

### 快速开始

**环境要求**
- Node.js (12 版本或更高)
- npm 或 yarn

**安装步骤**
```bash
# 克隆仓库
git clone [your-repo-url]

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build
```

### 开发心得

这个项目是我前端开发学习过程中的重要作品，它展示了：
- 组件化架构设计
- 状态管理模式
- API 集成
- 响应式设计原则
- 现代 JavaScript (ES6+) 应用

### 项目亮点

1. **完整的用户体验流程** - 从注册登录到视频观看，每个环节都经过精心设计
2. **模块化代码组织** - 清晰的文件结构，便于维护和扩展
3. **性能优化** - 路由懒加载、组件按需引入等优化策略
4. **安全考虑** - RSA 加密、表单验证等安全措施

### 技术难点与解决方案

- **视频播放优化**: 使用 XGPlayer 实现流畅的视频播放体验
- **状态管理**: 通过 Vuex 管理复杂的应用状态
- **组件通信**: 合理使用 props、events 和 Vuex 实现组件间通信
- **路由管理**: 实现路由懒加载和权限控制

---

### License | 许可证

MIT License
