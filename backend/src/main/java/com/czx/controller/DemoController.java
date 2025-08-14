package com.czx.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoController {

//    访问地址 http://10.4.43.57:9090/index.html
//    没有配置地址，这里不起作用
//    @RequestMapping("demo")
//    public String demo() {
//        System.out.println("进入controller中的demo方法！");
//        //如果不在appliation.yml文件中添加前后缀信息，此处返回语句为
//        //return "html/myPage.html"
//        return "index";
//    }

//    @GetMapping("/index")
//    public String index() {
//        return "index";
//    }

//    @GetMapping("wgt")
//    public String wgt() {
//        return "index";
//    }
}