package com.czx.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static String signKey = "itheima";
    private static Long expire = 43200000L;

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return
     */
    public static String generateJwt(Map<String, Object> claims){
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt){
        Claims claims = Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
    
    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID，如果获取失败返回null
     */
    public static Integer getUserIdFromToken(String token) {
        try {
            Claims claims = parseJWT(token);
            if (claims != null) {
                Object userIdObj = claims.get("userId");
                if (userIdObj != null) {
                    if (userIdObj instanceof Integer) {
                        return (Integer) userIdObj;
                    } else if (userIdObj instanceof String) {
                        return Integer.parseInt((String) userIdObj);
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败，返回null
        }
        return null;
    }
    
    /**
     * 从token中获取用户名
     * @param token JWT token
     * @return 用户名，如果获取失败返回null
     */
    public static String getUsernameFromToken(String token) {
        try {
            Claims claims = parseJWT(token);
            if (claims != null) {
                Object usernameObj = claims.get("username");
                if (usernameObj != null) {
                    return usernameObj.toString();
                }
            }
        } catch (Exception e) {
            // 解析失败，返回null
        }
        return null;
    }
    
    /**
     * 验证token是否有效
     * @param token JWT token
     * @return true表示有效，false表示无效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseJWT(token);
            if (claims != null) {
                // 检查是否过期
                Date expiration = claims.getExpiration();
                if (expiration != null && expiration.after(new Date())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 解析失败，返回false
        }
        return false;
    }
}
