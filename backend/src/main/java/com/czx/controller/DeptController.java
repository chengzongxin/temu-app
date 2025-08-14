package com.czx.controller;

import com.czx.pojo.Dept;
import com.czx.pojo.Emp;
import com.czx.pojo.Result;
import com.czx.service.DeptService;
import com.czx.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depts")
public class DeptController {
    @Autowired
    private DeptService deptService;
    @Autowired
    private EmpService empService;
    @GetMapping
    public Result depts() {
        System.out.println("hello world!");
        return Result.success(deptService.selectAll());
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result delete(@PathVariable Integer id) throws Exception {
        deptService.delete(id);

//        if (true) {
//            throw new Exception("error!!!!");
//        }
        empService.deleteByDeptId(id);
        return Result.success();
    }

    @PostMapping
    public Result add(@RequestBody Dept dept) {
        deptService.add(dept);
        return Result.success();
    }
}
