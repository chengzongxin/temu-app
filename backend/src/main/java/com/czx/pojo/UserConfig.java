package com.czx.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserConfig {
    private Integer id;
    private Integer user_id;
    private String kuajingmaihuo_cookie;
    private String agentseller_cookie;
    private String mallid;
    private String parent_msg_id;
    private String parent_msg_timestamp;
    private String tool_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
