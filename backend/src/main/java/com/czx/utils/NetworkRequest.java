package com.czx.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPInputStream;

@Slf4j
@Component
public class NetworkRequest {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public NetworkRequest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 获取请求头
     */
    private HttpHeaders getHeaders(String cookie, String mallid, String origin) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "*/*");
        // 移除accept-encoding，让服务器返回未压缩的响应
        headers.set("accept-encoding", "gzip, deflate, br, zstd");
        headers.set("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.set("anti-content", "");
        headers.set("cache-control", "max-age=0");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("cookie", cookie);
        headers.set("mallid", mallid);
        headers.set("origin", origin);
        headers.set("referer", origin);
        headers.set("sec-ch-ua", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"");
        headers.set("sec-ch-ua-mobile", "?1");
        headers.set("sec-ch-ua-platform", "\"Android\"");
        headers.set("sec-fetch-dest", "empty");
        headers.set("sec-fetch-mode", "cors");
        headers.set("sec-fetch-site", "same-origin");
        headers.set("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Mobile Safari/537.36");
        
        return headers;
    }
    
    /**
     * 解压gzip数据
     */
    private String decompressGzip(byte[] compressedData) throws IOException {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
            return new String(gzipInputStream.readAllBytes());
        }
    }
    
    /**
     * 处理响应体，自动检测并解压gzip数据
     */
    private String processResponseBody(byte[] responseBody) throws IOException {
        if (responseBody == null || responseBody.length == 0) {
            return null;
        }
        
        // 检查是否是gzip压缩数据（gzip文件头是 0x1f 0x8b）
        if (responseBody.length >= 2 && responseBody[0] == 0x1f && responseBody[1] == (byte) 0x8b) {
            log.info("检测到gzip压缩数据，正在解压...");
            return decompressGzip(responseBody);
        } else {
            // 不是压缩数据，直接返回字符串
            return new String(responseBody);
        }
    }
    
    /**
     * 发送POST请求
     * 返回类型改为Optional<JsonNode>，与Python版本保持一致
     */
    public Optional<JsonNode> post(String url, Map<String, Object> data, 
                                  String cookie, String mallid, String origin) {
        try {
            HttpHeaders headers = getHeaders(cookie, mallid, origin);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
            
            // 调试信息
            log.info("请求头: {}", headers);
            log.info("发送POST请求到: {}", url);
            log.info("请求数据: {}", data);
            
            ResponseEntity<byte[]> response = restTemplate.postForEntity(url, request, byte[].class);
            
            // 详细的错误处理
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("HTTP错误: {}", response.getStatusCode());
                log.error("响应内容: {}...", 
                    response.getBody() != null ? 
                    new String(response.getBody()).substring(0, Math.min(500, response.getBody().length)) : "null");
                
                // 尝试解析错误响应
                try {
                    if (response.getBody() != null) {
                        String responseText = processResponseBody(response.getBody());
                        if (responseText != null) {
                            JsonNode errorData = objectMapper.readTree(responseText);
                            log.error("错误详情: {}", errorData);
                        }
                    }
                } catch (Exception e) {
                    log.error("无法解析错误响应为JSON");
                }
                
                return Optional.empty();
            }
            
            // 处理响应体
            if (response.getBody() != null) {
                try {
                    String responseText = processResponseBody(response.getBody());
                    if (responseText != null) {
                        JsonNode result = objectMapper.readTree(responseText);
                        log.info("请求成功");
                        return Optional.of(result);
                    } else {
                        log.error("响应体处理失败");
                        return Optional.empty();
                    }
                } catch (Exception e) {
                    log.error("JSON解析错误: {}", e.getMessage());
                    if (response.getBody() != null) {
                        log.error("响应内容: {}...", 
                            new String(response.getBody()).substring(0, Math.min(500, response.getBody().length)));
                    }
                    return Optional.empty();
                }
            } else {
                log.error("响应体为空");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            log.error("POST请求失败: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * 发送GET请求
     * 返回类型改为Optional<JsonNode>，与Python版本保持一致
     */
    public Optional<JsonNode> get(String url, Map<String, Object> params,
                                 String cookie, String mallid, String origin) {
        try {
            HttpHeaders headers = getHeaders(cookie, mallid, origin);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            // 构建查询参数
            StringBuilder urlBuilder = new StringBuilder(url);
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                params.forEach((key, value) -> 
                    urlBuilder.append(key).append("=").append(value).append("&"));
                urlBuilder.setLength(urlBuilder.length() - 1); // 移除最后的&
            }
            
            String finalUrl = urlBuilder.toString();
            
            // 调试信息
            log.info("请求头: {}", headers);
            log.info("发送GET请求到: {}", finalUrl);
            log.info("请求参数: {}", params);
            
            ResponseEntity<byte[]> response = restTemplate.exchange(finalUrl, HttpMethod.GET, request, byte[].class);
            
            // 详细的错误处理
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("HTTP错误: {}", response.getStatusCode());
                log.error("响应内容: {}...", 
                    response.getBody() != null ? 
                    new String(response.getBody()).substring(0, Math.min(500, response.getBody().length)) : "null");
                return Optional.empty();
            }
            
            // 处理响应体
            if (response.getBody() != null) {
                try {
                    String responseText = processResponseBody(response.getBody());
                    if (responseText != null) {
                        JsonNode result = objectMapper.readTree(responseText);
                        log.info("请求成功");
                        return Optional.of(result);
                    } else {
                        log.error("响应体处理失败");
                        return Optional.empty();
                    }
                } catch (Exception e) {
                    log.error("JSON解析错误: {}", e.getMessage());
                    if (response.getBody() != null) {
                        log.error("响应内容: {}...", 
                            new String(response.getBody()).substring(0, Math.min(500, response.getBody().length)));
                    }
                    return Optional.empty();
                }
            } else {
                log.error("响应体为空");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            log.error("GET请求失败: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * 为了向后兼容，保留原有的方法
     * 但标记为已废弃
     */
    @Deprecated
    public ResponseEntity<String> postLegacy(String url, Map<String, Object> data, 
                                           String cookie, String mallid, String origin) {
        HttpHeaders headers = getHeaders(cookie, mallid, origin);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }
    
    @Deprecated
    public ResponseEntity<String> getLegacy(String url, Map<String, Object> params,
                                          String cookie, String mallid, String origin) {
        HttpHeaders headers = getHeaders(cookie, mallid, origin);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            params.forEach((key, value) -> 
                urlBuilder.append(key).append("=").append(value).append("&"));
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        
        return restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, request, String.class);
    }
}
