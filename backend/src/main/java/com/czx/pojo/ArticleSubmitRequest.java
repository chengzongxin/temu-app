package com.czx.pojo;

import lombok.Data;
import java.util.List;

@Data
public class ArticleSubmitRequest {
    private String title;
    private String content;
    private List<ArticleImageRequest> images;
    
    @Data
    public static class ArticleImageRequest {
        private Integer id;
        private Integer sortOrder;
    }
}
