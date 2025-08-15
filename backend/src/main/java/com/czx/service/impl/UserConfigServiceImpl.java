package com.czx.service.impl;

import com.czx.mapper.UserConfigMapper;
import com.czx.pojo.Result;
import com.czx.pojo.UserConfig;
import com.czx.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserConfigServiceImpl implements UserConfigService {
    
    @Autowired
    private UserConfigMapper userConfigMapper;
    
    @Override
    public boolean saveOrUpdateConfig(UserConfig config) {
        try {
            // 查找现有配置
            UserConfig existingConfig = userConfigMapper.findByUserId(config.getUser_id());
            
            if (existingConfig != null) {
                // 检查配置是否有变化
                boolean configChanged = (
                    !existingConfig.getAgentseller_cookie().equals(config.getAgentseller_cookie()) ||
                    !existingConfig.getMallid().equals(config.getMallid())
                );
                
                // 更新配置
                existingConfig.setAgentseller_cookie(config.getAgentseller_cookie());
                existingConfig.setMallid(config.getMallid());
                
                // 如果配置有变化，清除缓存数据
                if (configChanged) {
                    existingConfig.setParent_msg_id(null);
                    existingConfig.setParent_msg_timestamp(null);
                    existingConfig.setTool_id(null);
                }
                
                userConfigMapper.update(existingConfig);
                return true;
            } else {
                // 创建新配置
                userConfigMapper.insert(config);
                return true;
            }
        } catch (Exception e) {
            log.error("保存配置失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public UserConfig getConfigByUserId(Integer userId) {
        return userConfigMapper.findByUserId(userId);
    }

    @Override
    public boolean updateCache(Integer userId, String parentMsgId, String parentMsgTimestamp, String toolId) {
        try {
            UserConfig config = userConfigMapper.findByUserId(userId);

            // 只更新提供的字段
            if (parentMsgId != null) {
                config.setParent_msg_id(parentMsgId);
            }
            if (parentMsgTimestamp != null) {
                config.setParent_msg_timestamp(parentMsgTimestamp);
            }
            if (toolId != null) {
                config.setTool_id(toolId);
            }
            
            userConfigMapper.update(config);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deleteConfig(Integer userId) {
        try {
            userConfigMapper.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
