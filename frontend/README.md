
## ��️ 项目架构概览

### 前端技术栈
- **框架**: React 19 + TypeScript + Vite
- **UI库**: Ant Design 5.x
- **状态管理**: MobX + React Context
- **路由**: React Router DOM 6
- **构建工具**: Vite + ESLint

### 后端技术栈
- **框架**: Spring Boot 3.5.4 + Java 17
- **数据库**: MySQL + MyBatis
- **认证**: JWT Token
- **文件存储**: 本地存储 + 阿里云OSS
- **构建工具**: Maven

## 🚀 核心功能模块

### 1. 用户认证系统
- **登录/登出**: `/api/auth/login`, `/api/auth/me`
- **JWT Token认证**: 所有API请求都需要携带token
- **用户权限管理**: 支持管理员和普通用户角色

### 2. TEMU违规商品管理
- **违规商品列表**: `/api/temu/compliance/list` - 分页获取违规商品
- **违规商品总数**: `/api/temu/compliance/total` - 获取违规商品总数
- **商品搜索**: `/api/temu/seller/product` - 支持商品ID和名称搜索
- **批量下架**: `/api/temu/seller/offline` - 批量下架违规商品
- **商品详情**: 支持查看商品详细信息、关联商品搜索

### 3. 文件管理系统
- **文件上传**: `/api/files/upload` - 支持多文件上传
- **文件列表**: `/api/files/list` - 获取用户文件列表
- **文件下载**: `/api/files/download/{id}` - 下载指定文件
- **文件删除**: `/api/files/delete/{id}` - 删除指定文件
- **文件统计**: 支持按文件类型统计、大小统计

### 4. 用户配置管理
- **配置保存**: `/api/config` (POST) - 保存用户配置
- **配置获取**: `/api/config` (GET) - 获取用户配置
- **配置清除**: `/api/config` (DELETE) - 清除用户配置
- **配置状态**: `/api/config/status` - 获取配置完整性状态
- **缓存更新**: `/api/config/cache` - 更新缓存配置

### 5. 图库管理
- **图片搜索**: `/api/blue/search` - 支持图片名称搜索
- **图片展示**: 支持图片预览、批量操作

## 🔧 技术特点

### 前端特色
- **响应式设计**: 使用Ant Design组件库，支持移动端适配
- **状态管理**: 使用MobX进行文件管理状态管理，React Context管理全局状态
- **路由保护**: 实现了ProtectedRoute组件，未登录用户自动跳转登录页
- **全局通知**: 自定义通知系统，支持成功、错误、警告等类型
- **文件类型识别**: 智能识别文件类型并显示对应图标

### 后端特色
- **统一响应格式**: 所有API返回统一的Result格式 `{code, msg, data}`
- **用户认证中间件**: 实现了LoginCheckInterceptor和LoginCheckFilter
- **多线程支持**: 支持批量操作时的多线程处理
- **文件存储**: 支持本地存储和云存储两种方式
- **数据库设计**: 使用MyBatis进行数据访问，支持分页查询

## 📱 页面功能

### 主要页面
1. **违规商品** (`/compliance`) - 违规商品列表、筛选、批量下架
2. **商品搜索** (`/product`) - 商品搜索、状态查看、批量操作
3. **配置管理** (`/config`) - 用户配置管理、状态监控
4. **文件管理** (`/files`) - 文件上传下载、类型统计
5. **图库管理** (`/gallery`) - 图片搜索、预览、管理
6. **未发布记录** (`/unpublished`) - 未发布SKC记录管理

## �� 安全特性

- **JWT认证**: 所有API都需要有效的JWT token
- **用户隔离**: 用户只能访问自己的数据和配置
- **输入验证**: 前后端都有输入验证机制
- **错误处理**: 统一的错误处理和用户提示

这是一个功能完整的电商违规商品管理平台，主要用于TEMU平台的商品合规管理。项目架构清晰，技术选型合理，代码质量较高。

现在我已经熟悉了你的项目，请告诉我你需要我帮你开发什么功能？

# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type-aware lint rules:

```js
export default tseslint.config([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...

      // Remove tseslint.configs.recommended and replace with this
      ...tseslint.configs.recommendedTypeChecked,
      // Alternatively, use this for stricter rules
      ...tseslint.configs.strictTypeChecked,
      // Optionally, add this for stylistic rules
      ...tseslint.configs.stylisticTypeChecked,

      // Other configs...
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

You can also install [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) and [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) for React-specific lint rules:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default tseslint.config([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...
      // Enable lint rules for React
      reactX.configs['recommended-typescript'],
      // Enable lint rules for React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

# React + Vite 前端部署与打包说明

## 1. 本地开发

```bash
npm install
npm run dev
```

---

## 2. 打包构建（生产环境）

```bash
npm run build
```
- 构建后静态文件在 `dist/` 目录下。

---

## 3. 本地预览

```bash
npm run preview -- --host
```
- 其他电脑可通过 `http://你的IP:4173` 访问。

---

## 4. 用 nginx 部署静态文件（推荐生产环境）

1. 安装 nginx（如 Ubuntu: `sudo apt install nginx`）。
2. 将 `dist/` 目录内容上传到服务器（如 `/var/www/temu-frontend`）。
3. 配置 nginx：

```nginx
server {
    listen 80;
    server_name your.domain.com;
    root /var/www/temu-frontend;
    index index.html;
    location / {
        try_files $uri $uri/ /index.html;
    }
    location /api/ {
        proxy_pass http://127.0.0.1:8000/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
4. 重载 nginx：
```bash
sudo nginx -s reload
```

---

## 5. 访问方式
- 浏览器访问 `http://your.domain.com` 或 `http://服务器IP`。
- 前端所有 `/api` 请求会自动转发到后端。

---

## 6. 用 Docker 部署前端项目（推荐自动化部署）

### 1. 打包前端静态文件
```bash
npm install
npm run build
```

### 2. 构建 Docker 镜像
```bash
docker build -t temu-frontend .
```

### 3. 运行 Docker 容器
```bash
docker run -d -p 3000:80 temu-frontend
```
这样你就可以通过 http://localhost:3000 访问前端页面。

- 现在可以通过 `http://localhost` 或 `http://服务器IP` 访问前端页面。
- 如需自定义 Nginx 配置，可将 `nginx.conf` 拷贝到项目根目录，并取消 Dockerfile 中相关注释。

---

如需详细部署帮助，请查阅本项目文档或联系开发者。
