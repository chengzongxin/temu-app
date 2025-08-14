package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.pojo.UserConfig;
import com.czx.service.UserConfigService;
import com.czx.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class UserConfigController {
    
    @Autowired
    private UserConfigService userConfigService;
    
    @PostMapping
    public Result setConfig(@RequestBody UserConfig userConfig, HttpServletRequest request) {
        try {
            // 从request中获取用户ID
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("用户未认证");
            }
            
            userConfig.setUser_id(userId);
            boolean success = userConfigService.saveOrUpdateConfig(userConfig);
            if (success) {
                return Result.success("配置已保存");
            } else {
                return Result.error("配置保存失败");
            }
        } catch (Exception e) {
            return Result.error("配置保存失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    public Result getConfig(HttpServletRequest request) {
        try {
            // 从request中获取用户ID
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("用户未认证");
            }
            
            UserConfig config = userConfigService.getConfigByUserId(userId);
            if (config != null) {
                return Result.success(config);
            } else {
                return Result.error("未找到配置");
            }
        } catch (Exception e) {
            return Result.error("获取配置失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping
    public Result clearConfig(HttpServletRequest request) {
        try {
            // 从request中获取用户ID
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("用户未认证");
            }
            
            boolean success = userConfigService.deleteConfig(userId);
            if (success) {
                return Result.success("配置已清除");
            } else {
                return Result.error("配置清除失败");
            }
        } catch (Exception e) {
            return Result.error("配置清除失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/cache")
    public Result updateCache(@RequestBody Map<String, String> cacheData, HttpServletRequest request) {
        try {
            // 从request中获取用户ID
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("用户未认证");
            }
            
            boolean success = userConfigService.updateCache(
                userId, 
                cacheData.get("parentMsgId"), 
                cacheData.get("parentMsgTimestamp"), 
                cacheData.get("toolId")
            );
            if (success) {
                return Result.success("缓存已更新");
            } else {
                return Result.error("缓存更新失败");
            }
        } catch (Exception e) {
            return Result.error("缓存更新失败: " + e.getMessage());
        }
    }
}
