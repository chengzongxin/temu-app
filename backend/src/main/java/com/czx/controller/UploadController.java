package com.czx.controller;

import com.czx.mapper.ImageMapper;
import com.czx.pojo.*;
import com.czx.service.DynamicService;
import com.czx.utils.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Slf4j
@RestController
public class UploadController {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DOWNLOADS_DIR = USER_HOME + "/Downloads/";

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    DynamicService dynamicService;

    @PostMapping("/upload")
    public Result upload(MultipartFile image) throws Exception {
        System.out.println(image);
//        log.info("文件上传, 文件名: {}", image.getOriginalFilename());

        //调用阿里云OSS工具类进行文件上传
        String url = aliOSSUtils.upload(image);
//        log.info("文件上传完成,文件访问的url: {}", url);
        return Result.success(url);
    }

    @PostMapping("/upload/images")
    public Result uploadImages(MultipartFile file) throws Exception {
        // 这个file必须和前端的【file: （二进制）】命名一直，否则是null
        System.out.println(file);
//        log.info("文件上传, 文件名: {}", image.getOriginalFilename());

        //调用阿里云OSS工具类进行文件上传
        String url = aliOSSUtils.upload(file);

//        int size = (int) file.getSize();

//        不插入数据库
//        Image image = new Image();
//        image.setUrl(url);
//        image.setSize(size);
//        image.setCreateTime(LocalDateTime.now());
//        imageMapper.insert(image);
//        log.info("文件上传完成,文件访问的url: {}", url);
        return Result.success(url);
    }

    @GetMapping("imageList")
    public Result imageList() {
        List<Image> list = imageMapper.list();
//        PageBean pageBean = empService.page(page, pageSize, name,gender, begin, end);
        return Result.success(list);
    }

    @PostMapping("/localupload")
    public String localUpload(MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload.";
        }
        try {
            byte[] bytes = file.getBytes();
            File uploadedFile = new File(DOWNLOADS_DIR + file.getOriginalFilename());
            file.transferTo(uploadedFile);
            return "File uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed!";
        }
    }

    @PostMapping("/uploadoss")
    public Result uploadoss(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("Please select a file to upload.");
        }

        ArrayList<Image> res = new ArrayList<>();
        try {
            byte[] bytes = file.getBytes();
            File uploadedFile = new File(DOWNLOADS_DIR + "uploadoss/" + file.getOriginalFilename());
            file.transferTo(uploadedFile);

            // 假设你有一个名为script.py的Python脚本
            String pythonScriptPath = "/Users/joe.cheng/oss/main.py";

            ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonScriptPath);
            processBuilder.redirectErrorStream(true);

            try {
                Process process = processBuilder.start();

                // 读取Python脚本的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;


                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("https://pic.to8to.com")) {
                        String[] split = line.split(",");
                        int size = (int) file.getSize();
                        Image image = new Image();
                        String path = split[0];
                        image.setName(path.substring(path.lastIndexOf("/")+1));
                        image.setUrl(split[1]);
                        image.setSize(size);
                        image.setCreateTime(LocalDateTime.now());

                        res.add(image);
                        imageMapper.insert(image);
                    }
                }
//                dynamicService.insert(dynamic);

                // 等待Python脚本执行完成
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Python script exited with error code: " + exitCode);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return Result.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("File upload failed!");
        }
    }
}
