package com.czx.service;

import java.util.List;
import java.util.Map;

public interface TemuService {
    /**
     * 获取违规列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 违规列表数据
     */
    Map<String, Object> getComplianceList(Integer userId, int page, int pageSize);
    
    /**
     * 获取违规总数
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 违规总数
     */
    Integer getComplianceTotal(Integer userId, int page, int pageSize);
    
    /**
     * 获取商品列表
     * @param userId 用户ID
     * @param productIds 商品ID列表
     * @param productName 商品名称
     * @param page 页码
     * @param pageSize 每页大小
     * @return 商品列表数据
     */
    Map<String, Object> getProducts(Integer userId, List<Long> productIds, String productName, int page, int pageSize);
    
    /**
     * 批量下架商品
     * @param userId 用户ID
     * @param productIds 商品ID列表
     * @param maxThreads 最大线程数
     * @return 下架结果数据
     */
    Map<String, Object> offlineProducts(Integer userId, List<Long> productIds, int maxThreads);
}
