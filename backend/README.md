# Core Service - Spring Boot 项目

## 项目概述

这是一个基于Spring Boot的后端服务项目，集成了TEMU卖家工具、文件管理、用户配置等功能，并支持JWT Token认证。

## 主要功能模块

### 1. 用户认证 (`/api/user/*`)
- **POST** `/login` - 用户登录
- **POST** `/api/user/register` - 用户注册
- **GET** `/api/user/info/{id}` - 获取用户信息

### 2. 用户配置管理 (`/api/config/*`)
- **POST** `/api/config` - 保存/更新用户配置
- **GET** `/api/config` - 获取用户配置
- **DELETE** `/api/config` - 清除用户配置
- **POST** `/api/config/cache` - 更新缓存数据

### 3. 文件管理 (`/api/files/*`)
- **POST** `/api/files/upload` - 文件上传（支持最大10GB）
- **GET** `/api/files/list` - 获取文件列表
- **GET** `/api/files/download/{id}` - 文件下载
- **DELETE** `/api/files/delete/{id}` - 删除文件
- **GET** `/api/files/info/{id}` - 获取文件信息

### 4. TEMU功能 (`/api/temu/*`)
- **GET** `/api/temu/compliance/list` - 获取违规列表
- **GET** `/api/temu/compliance/total` - 获取违规总数
- **POST** `/api/temu/seller/product` - 查询商品
- **POST** `/api/temu/seller/offline` - 批量下架商品

### 5. 测试接口 (`/test/*`)
- **GET** `/test` - 基础测试
- **GET** `/test/config` - 配置模块测试
- **GET** `/test/files` - 文件管理测试
- **GET** `/test/temu` - TEMU功能测试

## 认证系统

### JWT Token认证
- 所有API接口（除测试接口外）都需要在请求头中携带`token`
- Token格式：`Authorization: Bearer <your-jwt-token>` 或直接在header中设置 `token: <your-jwt-token>`
- 认证失败会返回401状态码和相应的错误信息
- 使用现有的`LoginCheckInterceptor`进行统一认证处理

### 用户登录
- 使用用户名和密码进行登录
- 登录成功后返回JWT Token
- 默认管理员账户：`admin` / `admin123`

### 请求头示例
```http
GET /api/config HTTP/1.1
Host: localhost:8000
token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 认证流程
1. 前端在登录成功后获取JWT Token
2. 后续请求在Header中携带Token
3. `LoginCheckInterceptor`拦截器验证Token有效性
4. 验证成功后，从JWT中解析用户信息并存储在Request中
5. Controller中可以使用`RequestUtils`获取用户ID和用户名
6. 业务逻辑根据用户ID获取对应配置

### JWT结构要求
你的JWT需要包含以下字段：
```json
{
  "userId": 123,
  "username": "your_username",
  "email": "user@example.com",
  "exp": 1234567890
}
```

## 技术架构

- **框架**: Spring Boot 3.5.4
- **数据库**: MySQL + MyBatis
- **文件存储**: 本地磁盘存储
- **认证**: JWT Token + LoginCheckInterceptor
- **多线程**: 支持并发处理
- **跨域**: CORS支持

## 数据库表结构

### users 表
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    is_active INT DEFAULT 1 COMMENT '是否激活 (1:激活, 0:未激活)',
    is_admin INT DEFAULT 0 COMMENT '是否管理员 (1:是, 0:否)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### user_configs 表
```sql
CREATE TABLE user_configs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    kuajingmaihuo_cookie TEXT,
    agentseller_cookie TEXT,
    mallid VARCHAR(100),
    parent_msg_id VARCHAR(100),
    parent_msg_timestamp VARCHAR(100),
    tool_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### file_records 表
```sql
CREATE TABLE file_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    uploaded_by VARCHAR(50) NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 快速开始

### 1. 环境要求
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 配置数据库
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/temu_app
    username: your_username
    password: your_password
```

### 3. 初始化数据库
运行 `src/main/resources/sql/init.sql` 创建必要的表

### 4. 启动项目
```bash
mvn spring-boot:run
```

### 5. 访问接口
- 项目地址: http://localhost:8000
- 测试接口: http://localhost:8000/test

## 配置说明

### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10GB
      max-request-size: 11GB

file:
  upload-dir: ./uploads
```

### 跨域配置
已配置CORS支持，允许所有来源访问

## 使用示例

### 1. 用户登录
```bash
curl -X POST http://localhost:8000/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "passwordHash": "$2b$12$t9KUWZ03VEOHHbxVEakLDedk1YkFtSgJ9FkBo92nqNa63u.Vh1Wam"
  }'
```

### 2. 上传文件（需要认证）
```bash
curl -X POST http://localhost:8000/api/files/upload \
  -H "token: your-jwt-token" \
  -F "file=@/path/to/your/file.txt"
```

### 3. 设置用户配置（需要认证）
```bash
curl -X POST http://localhost:8000/api/config \
  -H "Content-Type: application/json" \
  -H "token: your-jwt-token" \
  -d '{
    "kuajingmaihuoCookie": "your_cookie",
    "agentsellerCookie": "your_cookie",
    "mallid": "your_mallid"
  }'
```

### 4. 批量下架商品（需要认证）
```bash
curl -X POST http://localhost:8000/api/temu/seller/offline \
  -H "Content-Type: application/json" \
  -H "token: your-jwt-token" \
  -d '{
    "productIds": [123456, 789012],
    "maxThreads": 4
  }'
```

### 5. 测试接口（无需认证）
```bash
curl http://localhost:8000/test
```

## 注意事项

1. **认证集成**: 使用现有的`LoginCheckInterceptor`进行JWT认证，前端需要在请求头中携带token
2. **JWT结构**: 确保JWT包含`userId`、`username`和`email`字段
3. **默认账户**: 系统默认创建管理员账户 `admin/admin123`
4. **密码安全**: 实际项目中应该使用BCrypt等加密算法处理密码
5. **文件存储**: 文件存储在项目根目录的 `uploads/` 文件夹中
6. **缓存机制**: 支持24小时缓存，自动过期
7. **多线程**: 批量下架支持多线程并发处理
8. **错误处理**: 统一的异常处理和错误返回格式
9. **拦截器**: 使用现有的`LoginCheckInterceptor`统一处理认证，避免重复代码

## 开发说明

### 项目结构
```
src/main/java/com/czx/
├── controller/          # 控制器层
│   ├── LoginController.java    # 登录控制器
│   ├── UserController.java     # 用户管理控制器
│   ├── UserConfigController.java # 用户配置控制器
│   ├── FileController.java     # 文件管理控制器
│   └── TemuController.java     # TEMU功能控制器
├── service/            # 服务层
│   ├── impl/          # 服务实现
│   │   ├── UserServiceImpl.java
│   │   ├── UserConfigServiceImpl.java
│   │   ├── FileServiceImpl.java
│   │   └── TemuServiceImpl.java
│   ├── UserService.java
│   ├── UserConfigService.java
│   ├── FileService.java
│   └── TemuService.java
├── mapper/             # 数据访问层
│   ├── UserMapper.java
│   ├── UserConfigMapper.java
│   └── FileRecordMapper.java
├── pojo/               # 实体类
│   ├── User.java
│   ├── UserConfig.java
│   ├── FileRecord.java
│   └── Result.java
├── interceptor/        # 拦截器（LoginCheckInterceptor）
└── utils/              # 工具类
```

### 添加新功能
1. 创建实体类 (pojo)
2. 创建Mapper接口和XML
3. 创建Service接口和实现
4. 创建Controller
5. 更新数据库表结构

### 认证集成
- 新接口会自动继承认证机制（通过`LoginCheckInterceptor`）
- 使用`RequestUtils.getUserId(request)`获取用户ID
- 使用`RequestUtils.getUsername(request)`获取用户名
- 使用`RequestUtils.isAuthenticated(request)`检查认证状态

## 故障排除

### 常见问题
1. **认证失败**: 检查token是否有效，是否在请求头中正确设置，JWT是否包含必要字段
2. **登录失败**: 检查用户名和密码是否正确，用户是否已激活
3. **数据库连接失败**: 检查数据库配置和网络连接
4. **文件上传失败**: 检查文件大小限制和存储权限

### 日志查看
```yaml
logging:
  level:
    com.czx: DEBUG
    org.springframework.jdbc.support.JdbcTransactionManager: DEBUG
```

## 更新日志

- **v1.0.0**: 初始版本，基础功能实现
- **v1.1.0**: 添加文件管理功能
- **v1.2.0**: 添加TEMU功能模块
- **v1.3.0**: 完善用户配置管理
- **v1.4.0**: 集成JWT认证系统（使用现有LoginCheckInterceptor）
- **v1.5.0**: 重构用户认证系统，从员工登录改为用户登录

## 联系方式

如有问题或建议，请联系开发团队。
