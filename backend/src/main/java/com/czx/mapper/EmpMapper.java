package com.czx.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.czx.pojo.Emp;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EmpMapper {
    List<Emp> selectAll();
//    @Select("select * from emp")
//    List<Emp> list();

    List<Emp> list(String name, Integer gender, LocalDate begin, LocalDate end);

    @Insert("insert into emp(username, name, gender, image, job, entrydate, dept_id, create_time, update_time) " +
            "values (#{username},#{name},#{gender},#{image},#{job},#{entrydate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);

    @Select("select * from emp where id = #{id}")
    Emp getById(Integer id);

    void update(Emp emp);
    @Select("select * from emp where username = #{username} and password = #{password}")
    Emp login(Emp emp);

    @Delete("delete from emp where dept_id = #{id}")
    void deleteByDeptId(Integer id);
}
