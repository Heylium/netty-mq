package com.helium.nettymq.broker.config;

import com.helium.nettymq.broker.cache.CommonCache;
import io.netty.util.internal.StringUtil;

public class TopicInfoLoader {

    private TopicInfo topicInfo;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("MQ_HOME is invalid!");
        }
        String topicJsonFilePath = basePath + "/broker/config/mq-topic.json";
        topicInfo = new TopicInfo();
    }
}
