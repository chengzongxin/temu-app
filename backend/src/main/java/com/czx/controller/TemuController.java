package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.pojo.OfflineRequest;
import com.czx.pojo.ProductQueryRequest;
import com.czx.service.TemuService;
import com.czx.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temu")
public class TemuController {
    
    @Autowired
    private TemuService temuService;
    
    @GetMapping("/compliance/list")
    public Result getComplianceList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int page_size,
            HttpServletRequest request) {
        
        // 检查用户是否已认证
        if (!RequestUtils.isAuthenticated(request)) {
            return Result.error("用户未认证");
        }
        
        // 获取用户ID
        Integer userId = RequestUtils.getUserId(request);
        if (userId == null) {
            return Result.error("无法获取用户信息");
        }
        
        try {
            // 调用Service层，传递用户ID
            var result = temuService.getComplianceList(userId, page, page_size);
            if ((Boolean) result.get("success")) {
                return Result.success(result.get("items"));
            } else {
                return Result.error((String) result.get("error"));
            }
        } catch (Exception e) {
            return Result.error("获取违规列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/compliance/total")
    public Result getComplianceTotal(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        
        // 检查用户是否已认证
        if (!RequestUtils.isAuthenticated(request)) {
            return Result.error("用户未认证");
        }
        
        // 获取用户ID
        Integer userId = RequestUtils.getUserId(request);
        if (userId == null) {
            return Result.error("无法获取用户信息");
        }
        
        try {
            // 调用Service层，传递用户ID
            Integer total = temuService.getComplianceTotal(userId, page, pageSize);
            return Result.success(total);
        } catch (Exception e) {
            return Result.error("获取违规总数失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/seller/product")
    public Result getProducts(@RequestBody ProductQueryRequest request, HttpServletRequest httpRequest) {
        // 检查用户是否已认证
        if (!RequestUtils.isAuthenticated(httpRequest)) {
            return Result.error("用户未认证");
        }
        
        // 获取用户ID
        Integer userId = RequestUtils.getUserId(httpRequest);
        if (userId == null) {
            return Result.error("无法获取用户信息");
        }
        
        try {
            // 调用Service层，传递用户ID
            var result = temuService.getProducts(
                userId,
                request.getProductIds(),
                request.getProductName(), 
                request.getPage(),
                request.getPageSize()
            );
            if ((Boolean) result.get("success")) {
                return Result.success(result.get("items"));
            } else {
                return Result.error((String) result.get("error"));
            }
        } catch (Exception e) {
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/seller/offline")
    public Result offlineProducts(@RequestBody OfflineRequest request, HttpServletRequest httpRequest) {
        // 检查用户是否已认证
        if (!RequestUtils.isAuthenticated(httpRequest)) {
            return Result.error("用户未认证");
        }
        
        // 获取用户ID
        Integer userId = RequestUtils.getUserId(httpRequest);
        if (userId == null) {
            return Result.error("无法获取用户信息");
        }
        
        try {
            // 调用Service层，传递用户ID
            var result = temuService.offlineProducts(userId, request.getProductIds(), request.getMax_threads());
            if ((Boolean) result.get("success")) {
                return Result.success(result);
            } else {
                return Result.error((String) result.get("error"));
            }
        } catch (Exception e) {
            return Result.error("批量下架失败: " + e.getMessage());
        }
    }
}
