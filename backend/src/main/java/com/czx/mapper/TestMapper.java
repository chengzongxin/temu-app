package com.czx.mapper;

import com.czx.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    User test();
}
