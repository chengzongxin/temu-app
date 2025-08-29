package com.czx.service.impl;

import com.czx.mapper.ViolationTypeMapper;
import com.czx.pojo.UserConfig;
import com.czx.pojo.ViolationType;
import com.czx.service.ComplianceStatusService;
import com.czx.service.TemuService;
import com.czx.service.UserConfigService;
import com.czx.utils.NetworkRequest;
import com.czx.utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TemuServiceImpl implements TemuService {
    
    private static final Logger log = LoggerFactory.getLogger(TemuServiceImpl.class);
    
    // 注入ViolationTypeMapper
    @Autowired
    private ViolationTypeMapper violationTypeMapper;
    
    @Autowired
    private UserConfigService userConfigService;
    
    @Autowired
    private NetworkRequest networkRequest;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ComplianceStatusService complianceStatusService;
    
    /**
     * 获取违规描述选项列表
     * @param userId 用户ID
     * @return 违规描述选项列表
     */
    @Override
    public List<Map<String, Object>> getViolationTypes(Integer userId) {
        log.info("获取违规描述选项列表，用户ID: {}", userId);
        
        // 从数据库获取违规类型列表
        List<ViolationType> violationTypes = violationTypeMapper.findAll();
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 添加"全部类型"选项
        Map<String, Object> allTypeOption = new HashMap<>();
        allTypeOption.put("label", "全部类型");
        allTypeOption.put("value", "");  // 使用空字符串代替null
        result.add(allTypeOption);
        
        // 添加从数据库获取的违规类型
        for (ViolationType type : violationTypes) {
            Map<String, Object> option = new HashMap<>();
            option.put("label", type.getDescription());
            option.put("value", type.getDescription());
            result.add(option);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getComplianceList(Integer userId, int page, int pageSize) {
        try {
            // 根据用户ID获取配置
            UserConfig config = userConfigService.getConfigByUserId(userId);
            if (config == null) {
                throw new RuntimeException("用户配置不存在");
            }
            
            String agentseller_cookie = config.getAgentseller_cookie();
            String mallid = config.getMallid();
            String origin_url = "https://agentseller.temu.com";
            String api_url = "https://agentseller.temu.com/mms/tmod_punish/agent/merchant_appeal/entrance/list";
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("page_num", page);
            payload.put("page_size", pageSize);
            payload.put("target_type", "goods");
            
            Optional<JsonNode> response = networkRequest.post(api_url, payload, agentseller_cookie, mallid, origin_url);
            if (response.isEmpty()) {
                throw new RuntimeException("获取数据失败");
            }
            
            JsonNode jsonNode = response.get();
            Boolean success = JsonUtils.getBoolean(jsonNode, "success");
            
            if (success != null && success) {
                JsonNode resultNode = JsonUtils.getNode(jsonNode, "result");
                if (resultNode != null) {
                    // 获取商品列表
                    JsonNode itemsNode = JsonUtils.getNode(resultNode, "punish_appeal_entrance_list");
                    if (itemsNode != null && itemsNode.isArray()) {
                        // 提取所有商品ID
                        List<Long> productIds = new ArrayList<>();
                        for (JsonNode item : itemsNode) {
                            Long spuId = JsonUtils.getLong(item, "spu_id");
                            if (spuId != null) {
                                productIds.add(spuId);
                            }
                        }
                        
                        // 获取这些商品的处理状态
                        Map<Long, Integer> statusMap = complianceStatusService.getStatusByProductIds(userId, productIds);
                        
                        // 将处理状态添加到商品数据中
                        for (JsonNode item : itemsNode) {
                            Long spuId = JsonUtils.getLong(item, "spu_id");
                            if (spuId != null) {
                                // 使用ObjectNode可以修改JsonNode
                                ((ObjectNode) item).put("processed_status", statusMap.getOrDefault(spuId, 0));
                            }
                        }
                    }
                    JsonNode punishAppealList = JsonUtils.getNode(resultNode, "punish_appeal_entrance_list");
                    if (punishAppealList != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("items", punishAppealList);
                        result.put("success", true);
                        return result;
                    }
                }
                throw new RuntimeException("数据格式错误");
            } else {
                String msg = JsonUtils.getString(jsonNode, "msg");
                throw new RuntimeException("获取数据失败: " + (msg != null ? msg : "未知错误"));
            }
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    @Override
    public Integer getComplianceTotal(Integer userId, int page, int pageSize) {
        try {
            // 根据用户ID获取配置
            UserConfig config = userConfigService.getConfigByUserId(userId);
            if (config == null) {
                throw new RuntimeException("用户配置不存在");
            }
            
            String agentseller_cookie = config.getAgentseller_cookie();
            String mallid = config.getMallid();
            String origin_url = "https://agentseller.temu.com";
            String api_url = "https://agentseller.temu.com/mms/tmod_punish/agent/merchant_appeal/entrance/list";
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("page_num", page);
            payload.put("page_size", pageSize);
            payload.put("target_type", "goods");
            
            Optional<JsonNode> response = networkRequest.post(api_url, payload, agentseller_cookie, mallid, origin_url);
            if (response.isEmpty()) {
                throw new RuntimeException("获取数据失败");
            }
            
            JsonNode jsonNode = response.get();
            Boolean success = JsonUtils.getBoolean(jsonNode, "success");
            
            if (success != null && success) {
                JsonNode resultNode = JsonUtils.getNode(jsonNode, "result");
                if (resultNode != null) {
                    Integer total = JsonUtils.getInteger(resultNode, "total");
                    if (total != null) {
                        return total;
                    }
                }
                throw new RuntimeException("数据格式错误");
            } else {
                String msg = JsonUtils.getString(jsonNode, "msg");
                throw new RuntimeException("获取数据失败: " + (msg != null ? msg : "未知错误"));
            }
            
        } catch (Exception e) {
            throw new RuntimeException("获取违规总数失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getProducts(Integer userId, List<Long> productIds, String productName, int page, int pageSize) {
        try {
            // 根据用户ID获取配置
            UserConfig config = userConfigService.getConfigByUserId(userId);
            if (config == null) {
                throw new RuntimeException("用户配置不存在");
            }
            
            String agentseller_cookie = config.getAgentseller_cookie();
            String mallid = config.getMallid();
            String origin_url = "https://agentseller.temu.com";
            String url = "https://agentseller.temu.com/visage-agent-seller/product/skc/pageQuery";
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("page", page);
            payload.put("pageSize", pageSize);
            
            if (productIds != null) {
                payload.put("productIds", productIds);
            }
            if (productName != null) {
                payload.put("productName", productName);
            }
            
            Optional<JsonNode> response = networkRequest.post(url, payload, agentseller_cookie, mallid, origin_url);
            if (response.isEmpty()) {
                throw new RuntimeException("查询失败");
            }
            
            JsonNode jsonNode = response.get();
            Boolean success = JsonUtils.getBoolean(jsonNode, "success");
            
            if (success != null && success) {
                JsonNode resultNode = JsonUtils.getNode(jsonNode, "result");
                if (resultNode != null) {
                    JsonNode itemsNode = JsonUtils.getNode(resultNode, "pageItems");
                    if (itemsNode != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("items", itemsNode);
                        result.put("success", true);
                        return result;
                    }
                }
                throw new RuntimeException("数据格式错误");
            } else {
                String msg = JsonUtils.getString(jsonNode, "msg");
                throw new RuntimeException("查询失败: " + (msg != null ? msg : "未知错误"));
            }
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    @Override
    public Map<String, Object> offlineProducts(Integer userId, List<Long> productIds, int maxThreads) {
        try {
            // 根据用户ID获取配置
            UserConfig config = userConfigService.getConfigByUserId(userId);
            if (config == null) {
                throw new RuntimeException("用户配置不存在");
            }
            
            // 调用批量下架方法
            return batchOfflineProducts(config, productIds, maxThreads);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    /**
     * 批量下架商品的核心逻辑
     * 参考Python版本的实现，包含完整的缓存机制、重试机制、轮询查询等
     */
    private Map<String, Object> batchOfflineProducts(UserConfig config, List<Long> productIds, int maxThreads) {
        try {
            String agentseller_cookie = config.getAgentseller_cookie();
            String mallid = config.getMallid();
            String parent_msg_id = config.getParent_msg_id();
            String tool_id = config.getTool_id();
            String parent_msg_timestamp = config.getParent_msg_timestamp();
            String origin_url = "https://agentseller.temu.com";
            
            // 检查缓存是否有效（24小时）
            boolean cache_valid = false;
            if (parent_msg_id != null && tool_id != null && parent_msg_timestamp != null) {
                try {
                    long cache_time = Long.parseLong(parent_msg_timestamp);
                    long current_time = System.currentTimeMillis();
                    if (current_time - cache_time < 24 * 60 * 60 * 1000) { // 24小时
                        cache_valid = true;
                    }
                } catch (NumberFormatException e) {
                    // 时间戳解析失败，缓存无效
                }
            }
            
            if (!cache_valid) {
                // 缓存无效，重新获取parent_msg_id和tool_id
                Map<String, Object> init_result = initializeOfflineSession(agentseller_cookie, mallid, origin_url);
                if (!(Boolean) init_result.get("success")) {
                    return init_result;
                }
                
                // 从init_result中获取新的parent_msg_id和tool_id
                parent_msg_id = (String) init_result.get("parentMsgId");
                tool_id = (String) init_result.get("toolId");
                
                // 更新用户配置中的缓存
                if (parent_msg_id != null && tool_id != null) {
                    userConfigService.updateCache(
                        config.getUser_id(), 
                        parent_msg_id, 
                        String.valueOf(System.currentTimeMillis()), 
                        tool_id
                    );
                }
            }
            
            if (parent_msg_id == null || tool_id == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "无法获取下架所需的parentMsgId或toolId");
                return result;
            }
            
            // 使用多线程处理商品下架
            int actual_threads = Math.min(maxThreads, productIds.size());
            ExecutorService executor = Executors.newFixedThreadPool(actual_threads);
            
            // 创建final变量用于lambda表达式
            final String final_parent_msg_id = parent_msg_id;
            final String final_tool_id = tool_id;
            
            try {
                // 提交所有任务
                List<CompletableFuture<Map<String, Object>>> futures = productIds.stream()
                    .map(productId -> CompletableFuture.supplyAsync(() -> 
                        processSingleProduct(productId, final_parent_msg_id, final_tool_id, agentseller_cookie, mallid, origin_url), executor))
                    .collect(Collectors.toList());
                
                // 等待所有任务完成
                List<Map<String, Object>> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
                
                // 统计结果
                long success_count = results.stream().filter(r -> (Boolean) r.get("success")).count();
                long total_count = results.size();
                
                Map<String, Object> result_data = new HashMap<>();
                result_data.put("success", true);
                result_data.put("message", String.format("批量下架完成，共处理 %d 个商品，%d 个下架成功", total_count, success_count));
                result_data.put("parentMsgId", parent_msg_id);
                result_data.put("toolId", tool_id);
                result_data.put("cacheUsed", cache_valid);
                result_data.put("threadInfo", Map.of(
                    "maxThreads", maxThreads,
                    "actualThreads", actual_threads,
                    "productCount", productIds.size()
                ));
                result_data.put("results", results);
                result_data.put("summary", Map.of(
                    "total", total_count,
                    "success", success_count,
                    "failed", total_count - success_count
                ));
                
                return result_data;
                
            } finally {
                executor.shutdown();
            }
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    /**
     * 初始化下架会话
     * 参考Python版本的完整实现
     */
    private Map<String, Object> initializeOfflineSession(String cookie, String mallid, String origin_url) {
        try {
            String init_url = "https://agentseller.temu.com/bg/cute/api/merchantService/chat/sendMessage";
            String query_url = "https://agentseller.temu.com/bg/cute/api/merchantService/chat/queryMessage";
            
            // 第一步：发送"商品下架"消息初始化对话
            Map<String, Object> init_payload = new HashMap<>();
            init_payload.put("contentType", 1);
            init_payload.put("content", "商品下架");
            
            Optional<JsonNode> init_response = networkRequest.post(init_url, init_payload, cookie, mallid, origin_url);
            if (init_response.isEmpty() || !JsonUtils.getBoolean(init_response.get(), "success")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "初始化下架对话失败");
                return result;
            }
            
            String init_msg_id = JsonUtils.getString(JsonUtils.getNode(init_response.get(), "result"), "msgId");
            if (init_msg_id == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "获取初始消息ID失败");
                return result;
            }
            
            // 第二步：查询消息获取客服回复（带重试机制）
            int max_retries = 5;
            String parent_msg_id = null;
            String tool_id = null;
            
            for (int retry = 0; retry < max_retries; retry++) {
                Map<String, Object> query_payload = new HashMap<>();
                query_payload.put("msgId", init_msg_id);
                query_payload.put("direction", 2);
                query_payload.put("limit", 20);
                
                Optional<JsonNode> query_response = networkRequest.post(query_url, query_payload, cookie, mallid, origin_url);
                if (query_response.isEmpty() || !JsonUtils.getBoolean(query_response.get(), "success")) {
                    if (retry == max_retries - 1) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("error", "查询客服回复失败");
                        return result;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                
                // 查找包含"发商品"按钮的消息
                JsonNode message_list = JsonUtils.getNode(JsonUtils.getNode(query_response.get(), "result"), "messageList");
                if (message_list != null && message_list.isArray()) {
                    for (JsonNode msg : message_list) {
                        String content = JsonUtils.getString(msg, "content");
                        Integer content_type = JsonUtils.getInteger(msg, "contentType");
                        Integer sender_type = JsonUtils.getInteger(msg, "senderType");
                        
                        // 检查是否是客服回复的消息（senderType=1001）
                        if (sender_type != null && sender_type == 1001 && content_type != null && content_type == 6) {
                            try {
                                // 尝试解析JSON内容
                                if (content != null && content.contains("toolId") && content.contains("btnText")) {
                                    parent_msg_id = JsonUtils.getString(msg, "msgId");
                                    // 尝试从内容中提取toolId
                                    Pattern toolIdPattern = Pattern.compile("\"toolId\":(\\d+)");
                                    var matcher = toolIdPattern.matcher(content);
                                    if (matcher.find()) {
                                        tool_id = matcher.group(1);
                                    }
                                    break;
                                }
                            } catch (Exception e) {
                                // 解析失败，继续检查
                            }
                        }
                    }
                }
                
                if (parent_msg_id != null && tool_id != null) {
                    break;
                }
                
                // 如果没找到，等待后重试
                if (retry < max_retries - 1) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            // 如果找不到按钮消息，尝试使用初始消息ID作为备用方案
            if (parent_msg_id == null) {
                parent_msg_id = init_msg_id;
            }
            
            // 如果还没有tool_id，重新获取工具列表
            if (tool_id == null) {
                String tool_list_url = "https://agentseller.temu.com/api/kiana/marvel-supplier/api/ultraman/chat/reception/querySelfServiceTools";
                Optional<JsonNode> tool_list_resp = networkRequest.post(tool_list_url, new HashMap<>(), cookie, mallid, origin_url);
                
                if (tool_list_resp.isPresent() && JsonUtils.getBoolean(tool_list_resp.get(), "success")) {
                    JsonNode tools = JsonUtils.getNode(JsonUtils.getNode(tool_list_resp.get(), "result"), "list");
                    if (tools != null && tools.isArray()) {
                        for (JsonNode tool : tools) {
                            String tool_name = JsonUtils.getString(tool, "toolName");
                            if ("商品下架".equals(tool_name)) {
                                tool_id = JsonUtils.getString(tool, "toolId");
                                break;
                            }
                        }
                    }
                }
            }
            
            if (parent_msg_id != null && tool_id != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("parentMsgId", parent_msg_id);
                result.put("toolId", tool_id);
                result.put("message", "初始化成功");
                return result;
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "无法获取下架所需的parentMsgId或toolId");
                return result;
            }
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "初始化下架会话失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 处理单个商品的下架
     * 参考Python版本的完整实现，包含轮询查询结果
     */
    private Map<String, Object> processSingleProduct(Long productId, String parent_msg_id, String tool_id,
                                                   String cookie, String mallid, String origin_url) {
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("success", false);
        result.put("message", "");
        result.put("details", new HashMap<>());
        
        try {
            // 1. 查询商品基础信息
            String product_info_url = "https://agentseller.temu.com/api/kiana/marvel-supplier/api/ultraman/chat/reception/queryProductSkcBasicInfo";
            Map<String, Object> product_info_payload = new HashMap<>();
            product_info_payload.put("productSkcId", productId);
            
            Optional<JsonNode> product_info_response = networkRequest.post(product_info_url, product_info_payload, cookie, mallid, origin_url);
            if (product_info_response.isEmpty() || !JsonUtils.getBoolean(product_info_response.get(), "success")) {
                result.put("message", "查询商品信息失败");
                return result;
            }
            
            JsonNode product_info = JsonUtils.getNode(product_info_response.get(), "result");
            String product_name = JsonUtils.getString(product_info, "productName");
            String product_img = JsonUtils.getString(product_info, "productPicture");
            
            // 2. 预检查是否可以下架
            String precheck_url = "https://agentseller.temu.com/api/kiana/marvel-supplier/api/ultraman/chat/reception/queryPreInterceptForToolSubmit";
            Map<String, Object> precheck_payload = new HashMap<>();
            precheck_payload.put("toolId", tool_id);
            precheck_payload.put("dataId", String.valueOf(productId));
            
            Optional<JsonNode> precheck_response = networkRequest.post(precheck_url, precheck_payload, cookie, mallid, origin_url);
            if (precheck_response.isEmpty() || !JsonUtils.getBoolean(precheck_response.get(), "success")) {
                result.put("message", "预检查失败");
                return result;
            }
            
            Integer intercept_code = JsonUtils.getInteger(JsonUtils.getNode(precheck_response.get(), "result"), "interceptCode");
            if (intercept_code == null || intercept_code != 0) {
                String intercept_msg = JsonUtils.getString(JsonUtils.getNode(precheck_response.get(), "result"), "interceptMsg");
                result.put("message", "无法下架：" + (intercept_msg != null ? intercept_msg : "未知错误"));
                return result;
            }
            
            // 3. 发送商品信息进行下架
            Map<String, Object> offline_content = new HashMap<>();
            offline_content.put("name", product_name != null ? product_name : "商品名称");
            offline_content.put("img", product_img != null ? product_img : "商品图片");
            offline_content.put("dataType", 1);
            offline_content.put("dataId", String.valueOf(productId));
            offline_content.put("toolId", tool_id);
            String offline_url = "https://agentseller.temu.com/bg/cute/api/merchantService/chat/sendMessage";
            Map<String, Object> offline_payload = new HashMap<>();
            // 确保parentMsgId是字符串类型，与Python版本保持一致
            offline_payload.put("parentMsgId", String.valueOf(parent_msg_id));
            offline_payload.put("contentType", 7);
            // 将content转换为JSON字符串，与Python版本保持一致
            try {
                String contentJson = objectMapper.writeValueAsString(offline_content);
                offline_payload.put("content", contentJson);
            } catch (Exception e) {
                log.error("序列化content失败: {}", e.getMessage());
                result.put("message", "序列化商品信息失败");
                return result;
            }
            
            Optional<JsonNode> offline_response = networkRequest.post(offline_url, offline_payload, cookie, mallid, origin_url);
            if (offline_response.isEmpty() || !JsonUtils.getBoolean(offline_response.get(), "success")) {
                result.put("message", "发送下架请求失败");
                return result;
            }
            
            String offline_msg_id = JsonUtils.getString(JsonUtils.getNode(offline_response.get(), "result"), "msgId");
            
            // 4. 轮询查询下架结果
            String query_url = "https://agentseller.temu.com/bg/cute/api/merchantService/chat/queryMessage";
            int max_retries = 10;
            int retry_count = 0;
            boolean offline_success = false;
            
            while (retry_count < max_retries) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
                // 查询下架结果
                Map<String, Object> result_query_payload = new HashMap<>();
                // 确保msgId是字符串类型，与Python版本保持一致
                result_query_payload.put("msgId", String.valueOf(offline_msg_id));
                result_query_payload.put("direction", 2);
                result_query_payload.put("limit", 20);
                
                Optional<JsonNode> result_query = networkRequest.post(query_url, result_query_payload, cookie, mallid, origin_url);
                if (result_query.isPresent() && JsonUtils.getBoolean(result_query.get(), "success")) {
                    JsonNode result_messages = JsonUtils.getNode(JsonUtils.getNode(result_query.get(), "result"), "messageList");
                    
                    // 查找当前商品的下架结果
                    String current_product_result = null;
                    if (result_messages != null && result_messages.isArray()) {
                        for (JsonNode msg : result_messages) {
                            String content = JsonUtils.getString(msg, "content");
                            if (content != null && content.contains("您好")) {
                                // 检查是否包含当前商品ID（支持多种格式）
                                if (content.contains("SKC ID：" + productId) || 
                                    content.contains("SKC ID:" + productId) ||
                                    content.contains("【SKC ID：" + productId + "】")) {
                                    current_product_result = content;
                                    break;
                                }
                            }
                        }
                    }
                    
                    // 如果找到了当前商品的结果
                    if (current_product_result != null) {
                        if (current_product_result.contains("已下架")) {
                            offline_success = true;
                            result.put("message", current_product_result);
                        } else if (current_product_result.contains("暂时无法操作下架")) {
                            result.put("message", current_product_result);
                        } else if (current_product_result.contains("已在您的上次咨询后处理成功")) {
                            result.put("message", current_product_result);
                            offline_success = true;  // 视为成功
                        } else {
                            result.put("message", current_product_result);
                        }
                        break;
                    }
                    
                    retry_count++;
                } else {
                    retry_count++;
                }
            }
            
            if (retry_count >= max_retries) {
                result.put("message", "查询下架结果超时");
            }
            
            result.put("success", offline_success);
            Map<String, Object> details = new HashMap<>();
            details.put("productName", product_name);
            details.put("productImg", product_img);
            details.put("offlineMsgId", offline_msg_id);
            details.put("retryCount", retry_count);
            result.put("details", details);
            
        } catch (Exception e) {
            result.put("message", "处理异常：" + e.getMessage());
        }
        
        return result;
    }
    
    // markProductStatus方法实现
    @Override
    public boolean markProductStatus(Integer userId, Long productId, Integer status) {
        return complianceStatusService.updateStatus(userId, productId, status);
    }
}
