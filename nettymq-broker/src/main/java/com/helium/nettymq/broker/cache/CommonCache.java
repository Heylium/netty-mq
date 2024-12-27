package com.helium.nettymq.broker.cache;

import com.helium.nettymq.broker.config.GlobalProperties;
import com.helium.nettymq.broker.config.TopicInfo;
import com.helium.nettymq.broker.model.MqTopicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一缓存对象
 */
public class CommonCache {

    public static GlobalProperties globalProperties = new GlobalProperties();

    public static List<MqTopicModel> mqTopicModelList = new ArrayList<>();

    public static TopicInfo topicInfo = new TopicInfo();

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(final GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static List<MqTopicModel> getMqTopicModelList() {
        return mqTopicModelList;
    }

    public static void setMqTopicModelList(List<MqTopicModel> mqTopicModelList) {
        CommonCache.mqTopicModelList = mqTopicModelList;
    }

    public static TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public static void setTopicInfo(final TopicInfo topicInfo) {
        CommonCache.topicInfo = topicInfo;
    }
}
