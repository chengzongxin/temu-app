-- 创建违规类型表
CREATE TABLE IF NOT EXISTS `violation_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_code` int NOT NULL COMMENT '违规类型编码',
  `description` varchar(255) NOT NULL COMMENT '违规描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_code`),
  UNIQUE KEY `uk_description` (`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违规类型表';

-- 插入违规类型数据
INSERT INTO `violation_type` (`type_code`, `description`) VALUES
(1, '疑似版权侵权'),
(2, '使用了未经授权的商标'),
(3, '商品信息存在成人类信息的内容'),
(4, '商品存在引导赌博活动的行为'),
(5, '卖家未经授权使用他人品牌或侵犯他人权利'),
(6, '该商品涉及医疗虚假宣传'),
(7, '您发布的商品违反了限制旗帜和徽章规则'),
(8, '存在商品重复铺货的情况');