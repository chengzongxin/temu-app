package com.czx.controller;

import com.czx.pojo.Result;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class DownloadController {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DOWNLOAD_DIRECTORY = USER_HOME + "/Downloads/";
    private static final String OSS_DIRECTORY = DOWNLOAD_DIRECTORY + "/uploadoss/";

    @GetMapping("/download/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Path filePath = Paths.get(DOWNLOAD_DIRECTORY).resolve(fileName).normalize();
        try {
            Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/uploadoss/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadOssFile(@PathVariable String fileName) {
        Path filePath = Paths.get(OSS_DIRECTORY).resolve(fileName).normalize();
        try {
            Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/fileList")
    public Result fileList()
    {
//        Path filePath = Paths.get(OSS_DIRECTORY);
//        List<String> files = new ArrayList<>();
//        try (BufferedReader br = Files.newBufferedReader(filePath)) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//                files.add(line);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }


        List fileList = new ArrayList<>();
        File dir = new File(OSS_DIRECTORY);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
                fileList.add(file.getName());
            }
        }

        return Result.success(fileList);
    }



    @GetMapping("/deleteOssFiles")
    public Result deleteOssFiles()
    {

        Path dir = Paths.get(OSS_DIRECTORY);
        try {
            Files.walk(dir)
                    .filter(path -> !path.equals(dir))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("删除成功");

        return Result.success();
    }



}
