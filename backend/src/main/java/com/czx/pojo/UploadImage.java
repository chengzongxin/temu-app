package com.czx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadImage {
    private Integer id; //ID
    private String name; //
    private String url; //
    private Integer size; //
    private LocalDateTime createTime; //创建时间
}
