package com.czx.utils;


import jakarta.servlet.http.HttpServletRequest;
/**
 * 请求工具类，用于从HttpServletRequest中获取用户信息
 */
public class RequestUtils {
    
    /**
     * 从request中获取用户ID
     */
    public static Integer getUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            if (userIdObj instanceof Integer) {
                return (Integer) userIdObj;
            } else if (userIdObj instanceof String) {
                try {
                    return Integer.parseInt((String) userIdObj);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * 从request中获取用户名
     */
    public static String getUsername(HttpServletRequest request) {
        Object usernameObj = request.getAttribute("username");
        if (usernameObj instanceof String) {
            return (String) usernameObj;
        }
        return null;
    }
    
    /**
     * 从request中获取token
     */
    public static String getToken(HttpServletRequest request) {
        return request.getHeader("token");
    }
    
    /**
     * 检查用户是否已认证
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getUserId(request) != null;
    }
}
