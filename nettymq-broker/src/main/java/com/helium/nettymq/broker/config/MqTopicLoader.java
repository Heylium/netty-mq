package com.helium.nettymq.broker.config;

import com.alibaba.fastjson2.JSON;
import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.model.MqTopicModel;
import com.helium.nettymq.broker.utils.FileContentReaderUtils;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.stream.Collectors;

public class MqTopicLoader {

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("EAGLE_MQ_HOME is invalid!");
        }
        String topicJsonFilePath = basePath + "/config/mq-topic.json";
        String fileContent = FileContentReaderUtils.readFromFile(topicJsonFilePath);
        List<MqTopicModel> mqTopicModelList = JSON.parseArray(fileContent, MqTopicModel.class);
        CommonCache.setMqTopicModelMap(mqTopicModelList.stream().collect(Collectors.toMap(MqTopicModel::getTopic, item -> item)));
    }
}
