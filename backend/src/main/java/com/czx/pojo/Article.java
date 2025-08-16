package com.czx.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Article {
    private Integer id;
    private String title;
    private String content;
    private Integer authorId;
    private Integer status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Integer reviewedBy;
    private String reviewComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联查询字段
    private String authorName;
    private String reviewerName;
    private List<ArticleImage> images;
    
    // 状态常量
    public static final int STATUS_SUBMITTED = 1;    // 已提交
    public static final int STATUS_REVIEWING = 2;    // 审核中
    public static final int STATUS_APPROVED = 3;     // 已通过
    public static final int STATUS_REJECTED = 4;     // 已拒绝
}
