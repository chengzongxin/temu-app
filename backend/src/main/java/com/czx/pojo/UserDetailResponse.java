package com.czx.pojo;

import java.util.List;

/**
 * 用户详细信息响应类
 * 包含用户基本信息和角色信息
 */
public class UserDetailResponse {
    private User user;
    private List<UserRoleRelation> roles;
    
    public UserDetailResponse() {}
    
    public UserDetailResponse(User user, List<UserRoleRelation> roles) {
        this.user = user;
        this.roles = roles;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<UserRoleRelation> getRoles() {
        return roles;
    }
    
    public void setRoles(List<UserRoleRelation> roles) {
        this.roles = roles;
    }
}

