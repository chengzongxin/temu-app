package com.czx.service.impl;

import com.czx.mapper.DynamicMapper;
import com.czx.pojo.Dynamic;
import com.czx.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicServiceImpl implements DynamicService {
    @Autowired
    private DynamicMapper dynamicMapper;
    @Override
    public void insert(Dynamic dynamic) {
        dynamicMapper.insert(dynamic);
    }

    @Override
    public Dynamic[] list() {
        return dynamicMapper.list();
    }

    @Override
    public void update(Dynamic dynamic) {
        dynamicMapper.update(dynamic);
    }
}
