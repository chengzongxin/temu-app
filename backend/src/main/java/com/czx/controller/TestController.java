package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    TestService testService;

    @GetMapping("/test")
    public Result test() {
        return Result.success(testService.test());
    }
    
    @GetMapping("/test/config")
    public Result testConfig() {
        return Result.success("配置测试接口正常");
    }
    
    @GetMapping("/test/files")
    public Result testFiles() {
        return Result.success("文件管理测试接口正常");
    }
    
    @GetMapping("/test/temu")
    public Result testTemu() {
        return Result.success("TEMU功能测试接口正常");
    }
}
