package com.czx.service.impl;

import com.czx.mapper.UserRoleMapper;
import com.czx.mapper.UserMapper;
import com.czx.pojo.UserRole;
import com.czx.pojo.UserRoleRelation;
import com.czx.pojo.User;
import com.czx.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<UserRole> getAllRoles() {
        return userRoleMapper.getAllRoles();
    }
    
    @Override
    public UserRole getRoleByCode(String roleCode) {
        return userRoleMapper.getRoleByCode(roleCode);
    }
    
    @Override
    public List<UserRoleRelation> getUserRoles(Integer userId) {
        return userRoleMapper.getUserRoles(userId);
    }
    
    @Override
    public boolean hasRole(Integer userId, String roleCode) {
        return userRoleMapper.hasRole(userId, roleCode);
    }
    
    @Override
    @Transactional
    public boolean assignRole(Integer userId, String roleCode) {
        UserRole role = getRoleByCode(roleCode);
        if (role == null) {
            return false;
        }
        
        try {
            userRoleMapper.assignRole(userId, role.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean removeRole(Integer userId, String roleCode) {
        UserRole role = getRoleByCode(roleCode);
        if (role == null) {
            return false;
        }
        
        try {
            userRoleMapper.removeRole(userId, role.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean createStudent(String username, String email, String password) {
        try {
            // 创建用户
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setPassword_hash(password); // 实际项目中应该加密
            user.setIs_active(1);
            user.setIs_admin(0);
            
            userMapper.insert(user);
            
            // 分配学生角色
            return assignRole(user.getId(), "STUDENT");
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean createReviewer(String username, String email, String password) {
        try {
            // 创建用户
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setPassword_hash(password); // 实际项目中应该加密
            user.setIs_active(1);
            user.setIs_admin(0);
            
            userMapper.insert(user);
            
            // 分配审核员角色
            return assignRole(user.getId(), "REVIEWER");
        } catch (Exception e) {
            return false;
        }
    }
}
