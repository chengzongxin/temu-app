package com.czx.config;

import com.czx.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //配置类
public class WebConfig implements WebMvcConfigurer {
    static final String ORIGINS[] = new String[] { "GET", "POST", "PUT", "DELETE" };

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录检查拦截器
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/test/**", "/health", "/", "/upload/**", "/download/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        /**
         * 所有请求都允许跨域，使用这种配置就不需要
         * 在interceptor中配置header了
         */
        corsRegistry.addMapping("/**") // 所有的当前站点的请求地址，都支持跨域访问。
                // allowOrigins 使用通配符时需要设置为false
                .allowCredentials(false)// 是否支持跨域用户凭证
                .allowedOrigins("*")// 所有的外部域都可跨域访问。 如果是localhost则很难配置，因为在跨域请求的时候，外部域的解析可能是localhost、127.0.0.1、主机名
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")// 当前站点支持的跨域请求类型是什么
                .allowedHeaders("*")
                .maxAge(3600);// 超时时长设置为1小时。 时间单位是秒。
    }
}
