package com.czx.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.czx.pojo.OperateLog;

import java.util.List;

@Mapper
public interface OperateLogMapper {

    //插入日志数据
    @Insert("insert into operate_log (operate_user, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "values (#{operateUser}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime});")
    public void insert(OperateLog log);

//    @Select("select * from operate_log")
//    @Select("select * from operate_log left join emp e on e.id = operate_log.operate_user")
    @Select("select operate_user, operate_time, class_name, method_name, method_params, return_value, cost_time, username, operate_username from operate_log left join users u on u.id = operate_log.operate_user  order by operate_time desc")
    List<OperateLog> getLog();
}
