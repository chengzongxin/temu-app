package com.czx.pojo;

import lombok.Data;
import java.util.List;

@Data
public class OfflineRequest {
    private List<Long> productIds;  // 改为Long类型处理大数值
    private Integer max_threads;
    
    // 为了向后兼容，添加getter方法
    public List<Integer> getProductIdsAsInteger() {
        if (productIds == null) return null;
        return productIds.stream()
            .map(Long::intValue)
            .collect(java.util.stream.Collectors.toList());
    }
}
