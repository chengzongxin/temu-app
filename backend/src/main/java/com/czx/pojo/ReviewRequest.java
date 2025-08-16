package com.czx.pojo;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer status;
    private String reviewComment;
}
