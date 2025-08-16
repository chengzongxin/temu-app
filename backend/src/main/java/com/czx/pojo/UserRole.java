package com.czx.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserRole {
    private Integer id;
    private String roleName;
    private String roleCode;
    private String description;
    private LocalDateTime createdAt;
}
