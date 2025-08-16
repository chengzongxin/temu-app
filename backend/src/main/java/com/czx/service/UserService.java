package com.czx.service;

import com.czx.pojo.User;
import java.util.List;

public interface UserService {
    /**
     * 用户登录
     * @param user 用户信息
     * @return 登录成功返回用户信息，失败返回null
     */
    User login(User user);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Integer id);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Integer id);
}
