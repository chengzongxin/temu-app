package com.czx.service.impl;

import com.czx.mapper.FileRecordMapper;
import com.czx.pojo.FileRecord;
import com.czx.pojo.Result;
import com.czx.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    
    @Autowired
    private FileRecordMapper fileRecordMapper;
    
    private final String uploadDir = "./uploads";
    
    public FileServiceImpl() {
        // 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    public FileRecord uploadFile(MultipartFile file, String uploadedBy) {
        try {
            if (file.isEmpty()) {
                return null;
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件到磁盘
            Path filePath = Paths.get(uploadDir, storedFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 创建文件记录
            FileRecord fileRecord = new FileRecord();
            fileRecord.setOriginal_name(originalFilename);
            fileRecord.setStored_name(storedFilename);
            fileRecord.setFile_path(filePath.toString());
            fileRecord.setFile_size(file.getSize());
            fileRecord.setFile_type(file.getContentType());
            fileRecord.setUploaded_by(uploadedBy);
            fileRecord.setUpload_time(LocalDateTime.now());
            
            // 保存到数据库
            fileRecordMapper.insert(fileRecord);
            
            return fileRecord;
            
        } catch (IOException e) {
            log.error("文件上传失败: " + e.getMessage());
            return null;
        }

    }

    @Override
    public List<FileRecord> getAllFiles() {
        return fileRecordMapper.findAll();
    }
    
    @Override
    public FileRecord getFileById(Integer id) {
        try {
            return fileRecordMapper.findById(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean deleteFile(Integer id) {
        try {
            FileRecord file = fileRecordMapper.findById(id);
            if (file == null) {
                System.out.println("文件不存在");
                return false;
            }
            
            // 删除物理文件
            Path filePath = Paths.get(file.getFile_path());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            // 删除数据库记录
            fileRecordMapper.deleteById(id);
            
            return true;
            
        } catch (Exception e) {
//            return Result.error("文件删除失败: " + e.getMessage());
            System.out.println(e.getMessage());
            return false;
        }

    }

    @Override
    public byte[] downloadFile(Integer id) {
        return new byte[0];
    }
}
