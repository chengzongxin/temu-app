package com.czx.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.czx.pojo.Dynamic;

@Mapper
public interface DynamicMapper {
    void insert(Dynamic dynamic);

    Dynamic[] list();

    void update(Dynamic dynamic);
}
