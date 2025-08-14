package com.czx.mapper;

import com.czx.pojo.UserConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserConfigMapper {
    UserConfig findByUserId(Integer userId);
    int insert(UserConfig userConfig);
    int update(UserConfig userConfig);
    int deleteByUserId(Integer userId);
}
