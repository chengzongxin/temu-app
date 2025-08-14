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
