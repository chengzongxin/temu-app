package com.czx.mapper;

import com.czx.pojo.FileRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FileRecordMapper {
    List<FileRecord> findAll();
    FileRecord findById(Integer id);
    int insert(FileRecord fileRecord);
    int deleteById(Integer id);
}
