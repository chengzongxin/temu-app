package com.czx.service;

import com.czx.pojo.UserConfig;

public interface UserConfigService {
    UserConfig getConfigByUserId(Integer userId);
    boolean saveOrUpdateConfig(UserConfig userConfig);
    boolean deleteConfig(Integer userId);
    boolean updateCache(Integer userId, String parentMsgId, String parentMsgTimestamp, String toolId);
}
