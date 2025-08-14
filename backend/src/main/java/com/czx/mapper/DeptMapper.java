package com.czx.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import com.czx.pojo.Dept;

import java.util.List;

@Mapper
public interface DeptMapper {
    List<Dept> selectAll();

    @Delete("delete from dept where id = #{id}")
    void delete(Integer id);

    @Insert("insert into dept(name,create_time,update_time) values(#{name},#{createTime},#{updateTime}) ")
    void add(Dept dept);
}
