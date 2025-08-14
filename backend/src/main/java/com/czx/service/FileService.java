package com.czx.service;

import com.czx.pojo.FileRecord;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {
    FileRecord uploadFile(MultipartFile file, String username);
    List<FileRecord> getAllFiles();
    FileRecord getFileById(Integer id);
    boolean deleteFile(Integer id);
    byte[] downloadFile(Integer id);
}
