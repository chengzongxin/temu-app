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
        try {
            FileRecord fileRecord = fileRecordMapper.findById(id);
            if (fileRecord == null) {
                log.warn("文件不存在，ID: {}", id);
                return null;
            }
            
            Path filePath = Paths.get(fileRecord.getFile_path());
            if (!Files.exists(filePath)) {
                log.warn("物理文件不存在: {}", fileRecord.getFile_path());
                return null;
            }
            
            return Files.readAllBytes(filePath);
        } catch (Exception e) {
            log.error("下载文件失败，ID: {}, 错误: {}", id, e.getMessage());
            return null;
        }
    }
    
    @Override
    public byte[] downloadFileByPath(String filePath) {
        try {
            // 构建完整的文件路径
            Path fullPath = Paths.get(filePath);
            
            // 安全检查：确保文件路径在uploads目录内
            Path uploadsDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path normalizedPath = fullPath.toAbsolutePath().normalize();
            
            if (!normalizedPath.startsWith(uploadsDir)) {
                log.warn("尝试访问uploads目录外的文件: {}", filePath);
                return null;
            }
            
            // 检查文件是否存在
            if (!Files.exists(normalizedPath)) {
                log.warn("文件不存在: {}", filePath);
                return null;
            }
            
            // 读取文件内容
            return Files.readAllBytes(normalizedPath);
            
        } catch (Exception e) {
            log.error("通过路径下载文件失败: {}", e.getMessage());
            return null;
        }
    }
}
