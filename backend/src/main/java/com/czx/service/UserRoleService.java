package com.czx.service;

import com.czx.pojo.UserRole;
import com.czx.pojo.UserRoleRelation;
import java.util.List;

public interface UserRoleService {
    
    // 获取所有角色
    List<UserRole> getAllRoles();
    
    // 根据角色代码获取角色
    UserRole getRoleByCode(String roleCode);
    
    // 获取用户的所有角色
    List<UserRoleRelation> getUserRoles(Integer userId);
    
    // 检查用户是否有指定角色
    boolean hasRole(Integer userId, String roleCode);
    
    // 为用户分配角色
    boolean assignRole(Integer userId, String roleCode);
    
    // 移除用户角色
    boolean removeRole(Integer userId, String roleCode);
    
    // 创建学生账号
    boolean createStudent(String username, String email, String password);
    
    // 创建审核员账号
    boolean createReviewer(String username, String email, String password);
}
