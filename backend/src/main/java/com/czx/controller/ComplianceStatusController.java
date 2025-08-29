package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.service.ComplianceStatusService;
import com.czx.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/temu/compliance")
public class ComplianceStatusController {

    @Autowired
    private ComplianceStatusService complianceStatusService;

    @PostMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        // 检查用户是否已认证
        if (!RequestUtils.isAuthenticated(request)) {
            return Result.error("用户未认证");
        }
        
        // 获取用户ID
        Integer userId = RequestUtils.getUserId(request);
        if (userId == null) {
            return Result.error("无法获取用户信息");
        }
        
        // 获取请求参数
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        
        try {
            boolean success = complianceStatusService.updateStatus(userId, productId, status);
            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("状态更新失败: " + e.getMessage());
        }
    }
}