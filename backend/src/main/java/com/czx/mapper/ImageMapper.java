package com.czx.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import com.czx.pojo.Dept;
import com.czx.pojo.Image;

import java.util.List;

@Mapper
public interface ImageMapper {
//    @Insert("insert into image_table (name, url, size, create_time) " +
//            "values (#{name}, #{url}, #{size}, #{createdTime});")
//    public void insert(UploadImage uploadImage);

//    void insert(UploadImage uploadImage);
//    @Insert("insert into image (url) " +
//            "values (#{url});")
//    public void insert(String url);

//    @Insert("insert into image(url, create_time) values (#{url},#{createTime});")
    void insert(Image image);

    @Insert("insert into dept(name,create_time,update_time) values(#{name},#{createTime},#{updateTime}) ")
    void add(Dept dept);

    List<Image> list();

//    @Insert("insert into dept(name,create_time,update_time) values(#{name},#{createTime},#{updateTime}) ")
//    void insert(Dept dept);
}
