package com.czx.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.czx.mapper.EmpMapper;
import com.czx.pojo.Emp;
import com.czx.pojo.PageBean;
import com.czx.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpMapper empMapper;
    @Override
    public List<Emp> selectAll() {
        return empMapper.selectAll();
    }

    @Override
    public PageBean page(Integer page,
                         Integer pageSize,
                         String name,
                         Integer gender,
                         LocalDate begin,
                         LocalDate end) {
        //1. 设置分页参数
        PageHelper.startPage(page,pageSize);
        //2. 执行查询
        List<Emp> list = empMapper.list(
                name,
                gender,
                begin,
                end);
        Page<Emp> p = (Page<Emp>) list;
        //3. 封装PageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public void insert(Emp emp) {
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.insert(emp);
    }

    @Override
    public Emp getById(Integer id) {
        return empMapper.getById(id);
    }

    @Override
    public void update(Emp emp) {
        empMapper.update(emp);
    }

    @Override
    public Emp login(Emp emp) {
        return empMapper.login(emp);
    }

    @Override
    public void deleteByDeptId(Integer id) {
        empMapper.deleteByDeptId(id);
    }
}
