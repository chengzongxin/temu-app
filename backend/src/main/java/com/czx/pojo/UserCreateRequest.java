package com.czx.pojo;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private String roleCode;  // STUDENT, REVIEWER, ADMIN
}
