package com.czx.mapper;

import com.czx.pojo.ViolationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 违规类型Mapper接口
 */
@Mapper
public interface ViolationTypeMapper {
    
    /**
     * 获取所有违规类型
     * @return 违规类型列表
     */
    @Select("SELECT id, type_code as typeCode, description FROM violation_type ORDER BY type_code")
    List<ViolationType> findAll();
}