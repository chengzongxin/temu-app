package com.czx.controller;

import lombok.extern.slf4j.Slf4j;
import com.czx.pojo.User;
import com.czx.pojo.Result;
import com.czx.service.UserService;
import com.czx.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
