package com.czx.mapper;

import com.czx.pojo.UserRole;
import com.czx.pojo.UserRoleRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserRoleMapper {
    
    // 获取所有角色
    List<UserRole> getAllRoles();
    
    // 根据角色代码获取角色
    UserRole getRoleByCode(@Param("roleCode") String roleCode);
    
    // 获取用户的所有角色
    List<UserRoleRelation> getUserRoles(@Param("userId") Integer userId);
    
    // 检查用户是否有指定角色
    boolean hasRole(@Param("userId") Integer userId, @Param("roleCode") String roleCode);
    
    // 为用户分配角色
    int assignRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
    
    // 移除用户角色
    int removeRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
