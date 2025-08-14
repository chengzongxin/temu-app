package com.czx.service.impl;

import com.czx.mapper.TestMapper;
import com.czx.pojo.User;
import com.czx.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestMapper testMapper;

    @Override
    public String test() {
        User user = testMapper.test();
        return user.toString();
    }
}
