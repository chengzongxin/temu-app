package com.czx.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 解析JSON字符串为JsonNode
     */
    public static JsonNode parseJson(String jsonString) throws JsonProcessingException {
        return objectMapper.readTree(jsonString);
    }
    
    /**
     * 解析JSON字符串为Map
     */
    public static Map<String, Object> parseJsonToMap(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, Map.class);
    }
    
    /**
     * 将对象转换为JSON字符串
     */
    public static String toJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * 从JsonNode中安全获取字符串值
     */
    public static String getString(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            return fieldNode.isTextual() ? fieldNode.asText() : fieldNode.toString();
        }
        return null;
    }
    
    /**
     * 从JsonNode中安全获取整数值
     * 优先尝试获取int，如果超出范围则返回null
     */
    public static Integer getInteger(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            if (fieldNode.isNumber()) {
                try {
                    // 先尝试获取long值，检查是否超出int范围
                    long longValue = fieldNode.asLong();
                    if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                        return (int) longValue;
                    } else {
                        // 超出int范围，返回null
                        return null;
                    }
                } catch (Exception e) {
                    // 转换失败，返回null
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * 从JsonNode中安全获取长整数值
     * 用于处理超出int范围的大数值
     */
    public static Long getLong(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            if (fieldNode.isNumber()) {
                try {
                    return fieldNode.asLong();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * 从JsonNode中安全获取数值（自动选择合适类型）
     * 优先返回Integer，如果超出范围则返回Long
     */
    public static Number getNumber(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            if (fieldNode.isNumber()) {
                try {
                    // 先尝试获取long值，检查是否超出int范围
                    long longValue = fieldNode.asLong();
                    if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                        return (int) longValue;
                    } else {
                        // 超出int范围，返回Long
                        return longValue;
                    }
                } catch (Exception e) {
                    // 转换失败，尝试其他方式
                    try {
                        return fieldNode.asDouble();
                    } catch (Exception ex) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 从JsonNode中安全获取布尔值
     */
    public static Boolean getBoolean(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            return fieldNode.isBoolean() ? fieldNode.asBoolean() : null;
        }
        return null;
    }
    
    /**
     * 从JsonNode中安全获取子节点
     */
    public static JsonNode getNode(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            return node.get(fieldName);
        }
        return null;
    }
    
    /**
     * 从JsonNode中安全获取数值，支持自动类型转换
     * 如果字段值超出int范围，会自动转换为Long
     */
    public static Object getNumericValue(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            JsonNode fieldNode = node.get(fieldName);
            if (fieldNode.isNumber()) {
                try {
                    // 先尝试获取long值，检查是否超出int范围
                    long longValue = fieldNode.asLong();
                    if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                        return (int) longValue;
                    } else {
                        // 超出int范围，返回Long
                        return longValue;
                    }
                } catch (Exception e) {
                    // 转换失败，尝试其他方式
                    try {
                        return fieldNode.asDouble();
                    } catch (Exception ex) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
