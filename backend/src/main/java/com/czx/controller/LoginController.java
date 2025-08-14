package com.czx.controller;

import lombok.extern.slf4j.Slf4j;
import com.czx.pojo.User;
import com.czx.pojo.Result;
import com.czx.service.UserService;
import com.czx.utils.JwtUtils;
import com.czx.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/auth/login")
    public Result login(@RequestBody User user){
        log.info("用户登录: {}", user);
        User u = userService.login(user);

        //登录成功,生成令牌,下发令牌
        if (u != null) {
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("userId", u.getId());
            claims.put("username", u.getUsername());
            claims.put("email", u.getEmail());

            String jwt = JwtUtils.generateJwt(claims); //jwt包含了当前登录用户的信息
            return Result.success(jwt);
        }
        //登录失败, 返回错误信息
        return Result.error("用户名或密码错误");
    }

    @GetMapping("/api/auth/me")
    public Result getCurrentUser(HttpServletRequest request){
        log.info("获取当前用户信息");
        
        // 从request中获取用户ID和用户名
        Integer userId = RequestUtils.getUserId(request);
        String username = RequestUtils.getUsername(request);
        
        if (userId == null || username == null) {
            return Result.error("用户未认证");
        }
        
        try {
            // 从数据库获取完整的用户信息
            User user = userService.findById(userId);
            if (user != null) {
                // 不返回敏感信息
                user.setPassword_hash(null);
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.error("获取用户信息失败");
        }
    }
}
