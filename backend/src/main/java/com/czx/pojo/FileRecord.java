package com.czx.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileRecord {
    private Integer id;
    private String original_name;
    private String stored_name;
    private String file_path;
    private Long file_size;
    private String file_type;
    private String uploaded_by;
    private LocalDateTime upload_time;
}
