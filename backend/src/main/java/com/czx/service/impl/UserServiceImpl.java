package com.czx.service.impl;

import com.czx.mapper.UserMapper;
import com.czx.pojo.User;
import com.czx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(User user) {
        // 根据用户名查找用户
        User foundUser = userMapper.findByUsername(user.getUsername());
        
        if (foundUser != null) {
            // 检查用户是否激活
            if (foundUser.getIs_active() != null && foundUser.getIs_active() == 0) {
                return null; // 用户未激活
            }
            
            // 检查密码是否匹配（这里假设密码是明文存储，实际应该使用加密）
            if (foundUser.getPassword().equals(user.getPassword())) {
                return foundUser;
            }
        }
        
        return null; // 登录失败
    }
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }
}
