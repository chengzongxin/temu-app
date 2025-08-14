package com.czx.service;

import com.czx.pojo.Dept;
import com.czx.pojo.Emp;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DeptService {
    List<Dept> selectAll();

    void delete(Integer id);

    void add(Dept dept);
}
