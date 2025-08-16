package com.czx.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleImage {
    private Integer id;
    private Integer articleId;
    private Integer imageId;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    
    // 关联查询字段
    private String originalName;
    private String downloadUrl;
    private Long fileSize;
    private String fileType;
}
