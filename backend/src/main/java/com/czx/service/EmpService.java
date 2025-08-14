package com.czx.service;

import com.czx.pojo.Emp;
import com.czx.pojo.PageBean;
import com.czx.pojo.Result;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public interface EmpService {
    public List<Emp> selectAll();

    PageBean page(Integer page,
                  Integer pageSize,
                  String name,
                  Integer gender,
                  LocalDate begin,
                  LocalDate end);

    void insert(Emp emp);

    Emp getById(Integer id);

    void update(Emp emp);

    Emp login(Emp emp);

    void deleteByDeptId(Integer id);
}
