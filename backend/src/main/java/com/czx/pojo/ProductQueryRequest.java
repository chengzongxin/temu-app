package com.czx.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ProductQueryRequest {
    private List<Long> productIds;  // 改为Long类型处理大数值
    private String productName;
    private Long page = 1L;         // 改为Long类型
    private Long pageSize = 500L;   // 改为Long类型
    
    // 为了向后兼容，添加getter方法
    public int getPage() {
        return page != null ? page.intValue() : 1;
    }
    
    public int getPageSize() {
        return pageSize != null ? pageSize.intValue() : 500;
    }
    
    public List<Integer> getProductIdsAsInteger() {
        if (productIds == null) return null;
        return productIds.stream()
            .map(Long::intValue)
            .collect(java.util.stream.Collectors.toList());
    }
}
