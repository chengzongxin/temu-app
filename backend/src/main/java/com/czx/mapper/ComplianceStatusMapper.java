package com.czx.mapper;

import com.czx.pojo.ComplianceStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ComplianceStatusMapper {
    
    @Insert("INSERT INTO compliance_status(user_id, product_id, status, processed_time) " +
            "VALUES(#{userId}, #{productId}, #{status}, NOW()) " +
            "ON DUPLICATE KEY UPDATE status = #{status}, processed_time = NOW()")
    void saveOrUpdate(ComplianceStatus status);
    
    @Select("SELECT * FROM compliance_status WHERE user_id = #{userId} AND product_id = #{productId}")
    ComplianceStatus findByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Long productId);
    
    @Select("SELECT * FROM compliance_status WHERE user_id = #{userId} AND product_id IN (${productIds})")
    List<ComplianceStatus> findByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIds") String productIds);
}