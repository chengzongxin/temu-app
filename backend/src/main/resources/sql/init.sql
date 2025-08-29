-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) COMMENT '密码哈希',
    is_active INT DEFAULT 1 COMMENT '是否激活 (1:激活, 0:未激活)',
    is_admin INT DEFAULT 0 COMMENT '是否管理员 (1:是, 0:否)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 插入默认管理员用户（密码: admin123）
INSERT INTO users (username, password, email, password_hash, is_active, is_admin, created_at, updated_at) 
VALUES ('admin', '1122', 'admin@temu-app.com', '', 1, 1, '2025-08-15 10:56:49', '2025-08-15 10:56:49');

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

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS operate_log (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    operate_user INT UNSIGNED COMMENT '操作人ID',
    operate_username VARCHAR(100) COMMENT '操作人姓名',
    operate_time DATETIME COMMENT '操作时间',
    class_name VARCHAR(100) COMMENT '操作的类名',
    method_name VARCHAR(100) COMMENT '操作的方法名',
    method_params TEXT COMMENT '方法参数',
    return_value VARCHAR(2000) COMMENT '返回值',
    cost_time BIGINT COMMENT '方法执行耗时, 单位:ms'
) COMMENT '操作日志表';

