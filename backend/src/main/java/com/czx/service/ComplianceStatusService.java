package com.czx.service;

import com.czx.pojo.ComplianceStatus;
import java.util.List;
import java.util.Map;

public interface ComplianceStatusService {
    
    /**
     * 更新商品处理状态
     * @param userId 用户ID
     * @param productId 商品ID
     * @param status 状态（0-未处理，1-已处理）
     * @return 更新结果
     */
    boolean updateStatus(Integer userId, Long productId, Integer status);
    
    /**
     * 批量获取商品处理状态
     * @param userId 用户ID
     * @param productIds 商品ID列表
     * @return 商品ID到状态的映射
     */
    Map<Long, Integer> getStatusByProductIds(Integer userId, List<Long> productIds);
}