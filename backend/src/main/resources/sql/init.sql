-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    is_active INT DEFAULT 1 COMMENT '是否激活 (1:激活, 0:未激活)',
    is_admin INT DEFAULT 0 COMMENT '是否管理员 (1:是, 0:否)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 插入默认管理员用户（密码: admin123）
INSERT INTO users (username, email, password_hash, is_active, is_admin) 
VALUES ('admin', 'admin@example.com', 'admin123', 1, 1)
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 用户配置表
CREATE TABLE IF NOT EXISTS user_configs (
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

-- 文件记录表
CREATE TABLE IF NOT EXISTS file_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    uploaded_by VARCHAR(50) NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 新增：用户角色表
CREATE TABLE IF NOT EXISTS user_roles (
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
('管理员', 'ADMIN', '系统管理员')
ON DUPLICATE KEY UPDATE created_at = NOW();

-- 新增：用户角色关联表
CREATE TABLE IF NOT EXISTS user_role_relations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_role (user_id, role_id)
);

-- 为现有管理员用户分配管理员角色
INSERT IGNORE INTO user_role_relations (user_id, role_id) 
SELECT u.id, r.id FROM users u, user_roles r 
WHERE u.username = 'admin' AND r.role_code = 'ADMIN';

-- 新增：文章表
CREATE TABLE IF NOT EXISTS articles (
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
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_author_id (author_id),
    INDEX idx_status (status),
    INDEX idx_submitted_at (submitted_at)
);

-- 新增：文章图片关联表
CREATE TABLE IF NOT EXISTS article_images (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL COMMENT '文章ID',
    image_id INT NOT NULL COMMENT '图片文件ID（关联file_records表）',
    sort_order INT DEFAULT 0 COMMENT '图片排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (image_id) REFERENCES file_records(id) ON DELETE CASCADE,
    INDEX idx_article_id (article_id),
    INDEX idx_sort_order (sort_order)
);
