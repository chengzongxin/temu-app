package com.czx.interceptor;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.czx.pojo.Result;
import com.czx.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        //1.获取请求url。
        String url = req.getRequestURL().toString();
        log.info("请求的url: {}",url);

        //2.判断请求url中是否包含login，如果包含，说明是登录操作，放行。
        if(url.contains("login")){
            log.info("登录操作, 放行...");
            return true;
        }

        // 不需要认证的路径
        if (url.contains("test") || url.contains("health")) {
            log.info("无需认证的路径, 放行...");
            return true;
        }

        //3.获取请求头中的令牌（token）。
        String jwt = req.getHeader("token");

        //4.判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if(!StringUtils.hasLength(jwt)){
            log.info("请求头token为空,返回未登录的信息");
            Result error = Result.error("NOT_LOGIN");
            //手动转换 对象--json --------> 阿里巴巴fastJSON
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        //5.解析token，如果解析失败，返回错误结果（未登录）。
        try {
            // 解析JWT获取用户信息
            Claims claims = JwtUtils.parseJWT(jwt);
            
            // 将用户信息存储到request中，供后续使用
            if (claims != null) {
                // 从Claims中提取用户ID和用户名
                // 这里需要根据你的JWT结构来调整字段名
                Object userIdObj = claims.get("userId");
                Object usernameObj = claims.get("username");
                
                if (userIdObj != null) {
                    if (userIdObj instanceof Integer) {
                        req.setAttribute("userId", userIdObj);
                    } else if (userIdObj instanceof String) {
                        try {
                            req.setAttribute("userId", Integer.parseInt((String) userIdObj));
                        } catch (NumberFormatException e) {
                            log.warn("用户ID格式错误: {}", userIdObj);
                        }
                    }
                }
                
                if (usernameObj != null) {
                    req.setAttribute("username", usernameObj.toString());
                }
                
                log.info("用户信息注入成功: userId={}, username={}", 
                    req.getAttribute("userId"), req.getAttribute("username"));
            }
            
        } catch (Exception e) {//jwt解析失败
            e.printStackTrace();
            log.info("解析令牌失败, 返回未登录错误信息");
            Result error = Result.error("NOT_LOGIN");
            //手动转换 对象--json --------> 阿里巴巴fastJSON
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        //6.放行。
        log.info("令牌合法, 放行");
        return true;
    }

    //    @Override //目标资源方法运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("postHandle ...");
    }

    @Override //视图渲染完毕后运行, 最后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("afterCompletion...");
    }
}
