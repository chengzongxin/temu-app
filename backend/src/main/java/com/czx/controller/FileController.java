package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.pojo.FileRecord;
import com.czx.service.FileService;
import com.czx.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // 从request中获取用户名
            String username = RequestUtils.getUsername(request);
            if (username == null) {
                return Result.error("用户未认证");
            }
            
            FileRecord fileRecord = fileService.uploadFile(file, username);
            return Result.success(fileRecord);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result listFiles(HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }

            List<FileRecord> files = fileService.getAllFiles();
            return Result.success(files);
        } catch (Exception e) {
            log.error("获取文件列表失败: " + e.getMessage());
            return Result.error("获取文件列表失败: " + e.getMessage());
        }
    }
    
    // 新增：通过文件路径下载文件
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFileByPath(
            @RequestParam String filePath, 
            HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return ResponseEntity.status(401).build();
            }
            
            // 根据文件路径获取文件数据
            byte[] fileData = fileService.downloadFileByPath(filePath);
            if (fileData == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 从文件路径中提取文件名
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Integer id, HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return ResponseEntity.status(401).build();
            }
            
            byte[] fileData = fileService.downloadFile(id);
            FileRecord fileRecord = fileService.getFileById(id);
            
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + fileRecord.getOriginal_name() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public Result deleteFile(@PathVariable Integer id, HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            boolean success = fileService.deleteFile(id);
            if (success) {
                return Result.success("文件删除成功");
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/info/{id}")
    public Result getFileInfo(@PathVariable Integer id, HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            FileRecord fileRecord = fileService.getFileById(id);
            if (fileRecord != null) {
                return Result.success(fileRecord);
            } else {
                return Result.error("文件不存在");
            }
        } catch (Exception e) {
            return Result.error("获取文件信息失败: " + e.getMessage());
        }
    }
}
