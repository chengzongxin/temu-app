package com.czx.controller;

import com.czx.mapper.OperateLogMapper;
import com.czx.pojo.OperateLog;
import com.czx.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LogController {
    @Autowired
    private OperateLogMapper operateLogMapper;
    @GetMapping("/getLog")
    public Result getLog(){
        List<OperateLog> list = operateLogMapper.getLog();
        return Result.success(list);
    }
}
