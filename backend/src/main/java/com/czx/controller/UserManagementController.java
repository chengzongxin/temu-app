package com.czx.controller;

import com.czx.pojo.*;
import com.czx.service.UserRoleService;
import com.czx.service.UserService;
import com.czx.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserManagementController {
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private UserService userService;
    
    // 创建学生账号
    @PostMapping("/api/users/students")
    public Result createStudent(@RequestBody UserCreateRequest request, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(httpRequest);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色
            if (!userRoleService.hasRole(userId, "ADMIN")) {
                return Result.error("只有管理员可以创建学生账号");
            }
            
            // 创建学生账号
            boolean success = userRoleService.createStudent(request.getUsername(), request.getEmail(), request.getPassword());
            if (success) {
                return Result.success("学生账号创建成功");
            } else {
                return Result.error("学生账号创建失败");
            }
            
        } catch (Exception e) {
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    // 创建审核员账号
    @PostMapping("/api/users/reviewers")
    public Result createReviewer(@RequestBody UserCreateRequest request, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(httpRequest);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色
            if (!userRoleService.hasRole(userId, "ADMIN")) {
                return Result.error("只有管理员可以创建审核员账号");
            }
            
            // 创建审核员账号
            boolean success = userRoleService.createReviewer(request.getUsername(), request.getEmail(), request.getPassword());
            if (success) {
                return Result.success("审核员账号创建成功");
            } else {
                return Result.error("审核员账号创建失败");
            }
            
        } catch (Exception e) {
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    // 获取所有角色
    @GetMapping("/api/users/roles")
    public Result getAllRoles(HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色
            if (!userRoleService.hasRole(userId, "ADMIN")) {
                return Result.error("只有管理员可以查看角色信息");
            }
            
            List<UserRole> roles = userRoleService.getAllRoles();
            return Result.success(roles);
            
        } catch (Exception e) {
            return Result.error("获取角色失败: " + e.getMessage());
        }
    }
    
    // 获取当前用户角色
    @GetMapping("/api/users/my/roles")
    public Result getMyRoles(HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer currentUserId = RequestUtils.getUserId(request);
            if (currentUserId == null) {
                return Result.error("无法获取用户信息");
            }
            
            List<UserRoleRelation> roles = userRoleService.getUserRoles(currentUserId);
            return Result.success(roles);
            
        } catch (Exception e) {
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }
    
    // 获取用户角色
    @GetMapping("/api/users/{userId}/roles")
    public Result getUserRoles(@PathVariable Integer userId, HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer currentUserId = RequestUtils.getUserId(request);
            if (currentUserId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色，或者查看自己的角色
            if (!userRoleService.hasRole(currentUserId, "ADMIN") && !currentUserId.equals(userId)) {
                return Result.error("只能查看自己的角色信息");
            }
            
            List<UserRoleRelation> roles = userRoleService.getUserRoles(userId);
            return Result.success(roles);
            
        } catch (Exception e) {
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }
    
    // 为用户分配角色
    @PostMapping("/api/users/{userId}/roles")
    public Result assignRole(@PathVariable Integer userId, @RequestParam String roleCode, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer currentUserId = RequestUtils.getUserId(httpRequest);
            if (currentUserId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色
            if (!userRoleService.hasRole(currentUserId, "ADMIN")) {
                return Result.error("只有管理员可以分配角色");
            }
            
            // 分配角色
            boolean success = userRoleService.assignRole(userId, roleCode);
            if (success) {
                return Result.success("角色分配成功");
            } else {
                return Result.error("角色分配失败");
            }
            
        } catch (Exception e) {
            return Result.error("分配角色失败: " + e.getMessage());
        }
    }
    
    // 移除用户角色
    @DeleteMapping("/api/users/{userId}/roles")
    public Result removeRole(@PathVariable Integer userId, @RequestParam String roleCode, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer currentUserId = RequestUtils.getUserId(httpRequest);
            if (currentUserId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色
            if (!userRoleService.hasRole(currentUserId, "ADMIN")) {
                return Result.error("只有管理员可以移除角色");
            }
            
            // 移除角色
            boolean success = userRoleService.removeRole(userId, roleCode);
            if (success) {
                return Result.success("角色移除成功");
            } else {
                return Result.error("角色移除失败");
            }
            
        } catch (Exception e) {
            return Result.error("移除角色失败: " + e.getMessage());
        }
    }
    
    // 获取所有用户信息（包含角色）
    @GetMapping("/api/users/all")
    public Result getAllUsers(HttpServletRequest request) {
        try {
            System.out.println("=== 开始获取所有用户 ===");
            
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                System.out.println("用户未认证");
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                System.out.println("无法获取用户信息");
                return Result.error("无法获取用户信息");
            }
            
            System.out.println("当前用户ID: " + userId);
            
            // 检查用户是否有管理员角色
            if (!userRoleService.hasRole(userId, "ADMIN")) {
                System.out.println("用户没有管理员角色");
                return Result.error("只有管理员可以查看用户信息");
            }
            
            System.out.println("用户有管理员角色，开始获取用户列表");
            List<User> users = userService.getAllUsers();
            System.out.println("获取到用户数量: " + (users != null ? users.size() : 0));
            
            return Result.success(users);
            
        } catch (Exception e) {
            System.err.println("获取用户列表异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    // 获取用户详细信息（包含角色）
    @GetMapping("/api/users/{userId}/detail")
    public Result getUserDetail(@PathVariable Integer userId, HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer currentUserId = RequestUtils.getUserId(request);
            if (currentUserId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有管理员角色，或者查看自己的信息
            if (!userRoleService.hasRole(currentUserId, "ADMIN") && !currentUserId.equals(userId)) {
                return Result.error("只能查看自己的用户信息");
            }
            
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 获取用户角色
            List<UserRoleRelation> roles = userRoleService.getUserRoles(userId);
            
            // 构建用户详细信息
            UserDetailResponse userDetail = new UserDetailResponse();
            userDetail.setUser(user);
            userDetail.setRoles(roles);
            
            return Result.success(userDetail);
            
        } catch (Exception e) {
            return Result.error("获取用户详情失败: " + e.getMessage());
        }
    }
}
