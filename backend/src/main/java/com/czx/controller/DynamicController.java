package com.czx.controller;

import com.czx.pojo.Dynamic;
import com.czx.pojo.Result;
import com.czx.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    DynamicService dynamicService;
    @PostMapping("/insert")
    public Result insert(@RequestBody Dynamic dynamic)
    {
        dynamicService.insert(dynamic);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list()
    {
        return Result.success(dynamicService.list());
    }

    @PostMapping("/update")
    public Result update(@RequestBody Dynamic dynamic)
    {
        dynamicService.update(dynamic);
        return Result.success();
    }

}
