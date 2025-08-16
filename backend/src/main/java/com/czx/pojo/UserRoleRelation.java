package com.czx.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserRoleRelation {
    private Integer id;
    private Integer userId;
    private Integer roleId;
    private LocalDateTime createdAt;
    
    // 关联查询字段
    private String username;
    private String roleName;
    private String roleCode;
}
