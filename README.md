我来帮你熟悉这个前后端分离项目。让我先仔细分析项目结构和功能。

# TEMU工具箱 - 前后端分离项目

## 项目概述

这是一个名为"TEMU工具箱"的前后端分离项目，主要用于TEMU卖家的商品管理和合规性检查。项目采用现代化的技术栈，具有完整的用户认证、文件管理、商品管理、文章管理等功能。

## 技术架构

### 前端技术栈
- **框架**: React 19 + TypeScript
- **状态管理**: MobX
- **UI组件库**: Ant Design 5.x
- **路由**: React Router 6
- **构建工具**: Vite
- **富文本编辑器**: wangEditor（支持图片上传）

### 后端技术栈
- **主要后端**: Java + Spring Boot 3.5.4
- **备用后端**: Python + FastAPI
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis
- **文件存储**: 本地存储 + 阿里云OSS
- **认证**: JWT Token

## 核心功能模块

### 1. 用户认证系统
- 用户登录/注册
- JWT Token认证
- 用户权限管理
- 用户配置管理

### 2. 商品合规管理
- 违规商品列表
- 商品搜索和筛选
- 商品详情查看
- 商品状态管理

### 3. 文件管理系统
- 文件上传/下载
- 文件类型验证
- 文件统计和分类
- 支持大文件（最大10GB）

### 4. 图库管理
- 图片上传和管理
- 图片预览和分类
- 图片搜索功能

### 5. 配置管理
- 用户个性化配置
- 系统参数设置
- 第三方服务配置

### 6. 文章管理系统（新增）
- 学生提交文章
- 审核员审核文章
- 文章状态管理
- 图片附件支持

## 项目结构

```
temu-app/
├── frontend/                    # React前端项目
│   ├── src/
│   │   ├── pages/             # 页面组件
│   │   ├── components/        # 通用组件
│   │   ├── contexts/          # React Context
│   │   ├── types/             # TypeScript类型定义
│   │   └── utils/             # 工具函数
│   └── package.json
├── backend/                    # Java后端项目
│   ├── src/main/java/         # Java源码
│   ├── src/main/resources/    # 配置文件和SQL脚本
│   └── pom.xml
└── README.md
```

## 完整部署步骤

### 步骤1：环境准备

#### 1.1 安装Java 17+
```bash
# 检查Java版本
java -version

# 如果没有安装，下载并安装Java 17+
# Windows: 下载JDK 17安装包
# Linux: sudo apt install openjdk-17-jdk
# macOS: brew install openjdk@17
```

#### 1.2 安装MySQL 8.0+
```bash
# 检查MySQL版本
mysql --version

# Windows: 下载MySQL安装包
# Linux: sudo apt install mysql-server
# macOS: brew install mysql

# 启动MySQL服务
# Windows: 在服务中启动MySQL
# Linux: sudo systemctl start mysql
# macOS: brew services start mysql
```

#### 1.3 安装Node.js 18+
```bash
# 检查Node.js版本
node --version
npm --version

# 如果没有安装，下载并安装Node.js 18+
# Windows: 下载Node.js安装包
# Linux: curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
# macOS: brew install node
```

#### 1.4 安装Maven 3.6+
```bash
# 检查Maven版本
mvn --version

# 如果没有安装，下载并安装Maven
# Windows: 下载Maven安装包
# Linux: sudo apt install maven
# macOS: brew install maven
```

### 步骤2：数据库初始化

#### 2.1 启动MySQL服务
```bash
# Windows: 在服务中启动MySQL
# Linux: sudo systemctl start mysql
# macOS: brew services start mysql
```

#### 2.2 创建数据库
```bash
mysql -u root -p
CREATE DATABASE temu_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

#### 2.3 执行初始化脚本
```bash
mysql -u root -p temu_app < backend/src/main/resources/sql/init.sql
```

### 步骤3：后端部署

#### 3.1 进入后端目录
```bash
cd backend
```

#### 3.2 修改数据库配置
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/temu_app
    username: your_username
    password: your_password
```

#### 3.3 编译项目
```bash
mvn clean compile
```

#### 3.4 启动后端服务
```bash
mvn spring-boot:run
```

#### 3.5 验证后端服务
```bash
curl http://localhost:8000/health
# 应该返回: {"status":"healthy"}
```

### 步骤4：前端部署

#### 4.1 进入前端目录
```bash
cd frontend
```

#### 4.2 安装依赖
```bash
npm install
```

#### 4.3 安装富文本编辑器
```bash
npm install @wangeditor/editor @wangeditor/editor-for-react
```

#### 4.4 修改API配置
编辑 `src/pages/api.ts`，确保API_BASE_URL指向后端地址：
```typescript
const API_BASE_URL = 'http://localhost:8000/api';
```

#### 4.5 启动开发服务器
```bash
npm run dev
```

#### 4.6 验证前端服务
浏览器访问 `http://localhost:5173`

### 步骤5：功能测试

#### 5.1 测试用户登录
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

#### 5.2 测试文件上传
```bash
# 先获取token
TOKEN="your-jwt-token-here"

curl -X POST http://localhost:8000/api/files/upload \
  -H "token: $TOKEN" \
  -F "file=@/path/to/test.txt"
```

#### 5.3 测试文章功能
在前端界面中测试：
- 文章创建
- 图片上传
- 文章编辑
- 文章审核

### 步骤6：生产环境部署

#### 6.1 构建前端
```bash
cd frontend
npm run build
```

#### 6.2 配置Nginx
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

#### 6.3 配置后端生产环境
编辑 `application.yml`：
```yaml
spring:
  profiles:
    active: production
  
  datasource:
    url: jdbc:mysql://production-db:3306/temu_app
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

server:
  port: 8000
```

#### 6.4 使用Docker部署（可选）
```bash
# 构建镜像
docker build -t temu-app .

# 运行容器
docker run -d -p 8000:8000 \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  temu-app
```

## 数据库表结构

### 现有表结构

#### users 表
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    is_active INT DEFAULT 1 COMMENT '是否激活',
    is_admin INT DEFAULT 0 COMMENT '是否管理员',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### file_records 表
```sql
CREATE TABLE file_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小',
    file_type VARCHAR(100) COMMENT '文件类型',
    uploaded_by VARCHAR(50) NOT NULL COMMENT '上传用户',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### user_configs 表
```sql
CREATE TABLE user_configs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    kuajingmaihuo_cookie TEXT COMMENT '跨境猫Cookie',
    agentseller_cookie TEXT COMMENT 'TEMU代理商Cookie',
    mallid VARCHAR(100) COMMENT 'MallId',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 新增文章管理表结构

#### user_roles 表
```sql
CREATE TABLE user_roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色代码',
    description TEXT COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入基础角色
INSERT INTO user_roles (role_name, role_code, description) VALUES
('学生', 'STUDENT', '可以提交文章'),
('审核员', 'REVIEWER', '可以审核文章'),
('管理员', 'ADMIN', '系统管理员');
```

#### articles 表
```sql
CREATE TABLE articles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    content TEXT NOT NULL COMMENT '文章内容（纯文本）',
    author_id INT NOT NULL COMMENT '作者ID',
    status INT DEFAULT 1 COMMENT '文章状态：1-已提交 2-审核中 3-已通过 4-已拒绝',
    submitted_at TIMESTAMP NULL COMMENT '提交时间',
    reviewed_at TIMESTAMP NULL COMMENT '审核时间',
    reviewed_by INT NULL COMMENT '审核员ID',
    review_comment TEXT COMMENT '审核意见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (reviewed_by) REFERENCES users(id)
);
```

#### article_images 表
```sql
CREATE TABLE article_images (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL COMMENT '文章ID',
    image_id INT NOT NULL COMMENT '图片文件ID（关联file_records表）',
    sort_order INT DEFAULT 0 COMMENT '图片排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES articles(id),
    FOREIGN KEY (image_id) REFERENCES file_records(id)
);
```

## API接口说明

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 文章管理接口
- `POST /api/articles/submit` - 提交文章
- `GET /api/articles/list` - 获取文章列表
- `GET /api/articles/{id}` - 获取文章详情
- `POST /api/articles/{id}/review` - 审核文章
- `PUT /api/articles/{id}` - 更新文章
- `DELETE /api/articles/{id}` - 删除文章

### 文件管理接口
- `POST /api/files/upload` - 文件上传
- `GET /api/files/list` - 获取文件列表
- `GET /api/files/download/{id}` - 文件下载
- `DELETE /api/files/delete/{id}` - 删除文件

## 富文本编辑器配置

### 安装依赖
```bash
npm install @wangeditor/editor @wangeditor/editor-for-react
```

### 基本使用
```typescript
import { Editor, Toolbar } from '@wangeditor/editor-for-react';
import { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor';

const ArticleEditor: React.FC = () => {
    const [editor, setEditor] = useState<IDomEditor | null>(null);
    const [html, setHtml] = useState('');
    
    // 编辑器配置
    const editorConfig: Partial<IEditorConfig> = {
        placeholder: '请输入内容...',
        MENU_CONF: {
            uploadImage: {
                server: '/api/files/upload',
                fieldName: 'file',
                headers: {
                    'token': getToken()
                }
            }
        }
    };
    
    return (
        <div>
            <Toolbar editor={editor} defaultConfig={toolbarConfig} />
            <Editor
                defaultConfig={editorConfig}
                value={html}
                onCreated={setEditor}
                onChange={editor => setHtml(editor.getHtml())}
            />
        </div>
    );
};
```

## 常见问题解决

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库连接配置
- 检查用户名密码是否正确

### 2. 前端无法连接后端
- 检查后端服务是否启动
- 检查API_BASE_URL配置
- 检查CORS配置

### 3. 文件上传失败
- 检查文件大小限制
- 检查存储目录权限
- 检查token是否有效

### 4. 富文本编辑器问题
- 检查依赖是否正确安装
- 检查图片上传配置
- 检查编辑器样式文件

## 开发指南

### 添加新功能
1. 创建数据库表
2. 创建实体类 (pojo)
3. 创建Mapper接口和XML
4. 创建Service接口和实现
5. 创建Controller
6. 创建前端组件和页面

### 代码规范
- 使用TypeScript进行类型检查
- 遵循ESLint规则
- 使用Prettier格式化代码
- 编写单元测试

## 更新日志

- **v1.0.0**: 初始版本，基础功能实现
- **v1.1.0**: 添加文件管理功能
- **v1.2.0**: 添加TEMU功能模块
- **v1.3.0**: 完善用户配置管理
- **v1.4.0**: 集成JWT认证系统
- **v1.5.0**: 重构用户认证系统
- **v2.0.0**: 新增文章管理系统，支持富文本编辑

## 联系方式

如有问题或建议，请联系开发团队。