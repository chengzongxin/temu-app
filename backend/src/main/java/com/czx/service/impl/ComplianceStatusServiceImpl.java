package com.czx.service.impl;

import com.czx.mapper.ComplianceStatusMapper;
import com.czx.pojo.ComplianceStatus;
import com.czx.service.ComplianceStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComplianceStatusServiceImpl implements ComplianceStatusService {

    @Autowired
    private ComplianceStatusMapper complianceStatusMapper;

    @Override
    public boolean updateStatus(Integer userId, Long productId, Integer status) {
        try {
            ComplianceStatus complianceStatus = new ComplianceStatus();
            complianceStatus.setUserId(userId);
            complianceStatus.setProductId(productId);
            complianceStatus.setStatus(status);
            
            complianceStatusMapper.saveOrUpdate(complianceStatus);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<Long, Integer> getStatusByProductIds(Integer userId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashMap<>();
        }
        
        // 将ID列表转换为逗号分隔的字符串
        String productIdsStr = productIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        List<ComplianceStatus> statusList = complianceStatusMapper.findByUserIdAndProductIds(userId, productIdsStr);
        
        // 将结果转换为Map
        Map<Long, Integer> resultMap = new HashMap<>();
        for (ComplianceStatus status : statusList) {
            resultMap.put(status.getProductId(), status.getStatus());
        }
        
        return resultMap;
    }
}