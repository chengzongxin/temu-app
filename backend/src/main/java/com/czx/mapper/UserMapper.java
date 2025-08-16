package com.czx.mapper;

import com.czx.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
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
     * 插入新用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> findAll();
}
