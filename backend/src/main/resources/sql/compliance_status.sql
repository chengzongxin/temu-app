-- 创建违规商品处理状态表
CREATE TABLE IF NOT EXISTS `compliance_status` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0-未处理，1-已处理',
    `processed_time` TIMESTAMP NULL COMMENT '处理时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `unique_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='违规商品处理状态表';