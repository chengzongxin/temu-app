package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.pojo.User;
import com.czx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 设置默认值
            if (user.getIs_active() == null) {
                user.setIs_active(1); // 默认激活
            }
            if (user.getIs_admin() == null) {
                user.setIs_admin(0); // 默认非管理员
            }
            
            // TODO: 这里应该对密码进行加密处理
            // 暂时使用明文密码（实际项目中应该使用BCrypt等加密）
            
            // 保存用户
            // 注意：这里需要实现UserService的insert方法
            // userService.insert(user);
            
            return Result.success("用户注册成功");
        } catch (Exception e) {
            return Result.error("用户注册失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/info/{id}")
    public Result getUserInfo(@PathVariable Integer id) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                // 不返回密码信息
                user.setPassword_hash(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
