package com.helium.nettymq.broker.config;

import com.alibaba.fastjson2.JSON;
import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.constants.BrokerConstants;
import com.helium.nettymq.broker.model.MqTopicModel;
import com.helium.nettymq.broker.utils.FileContentUtils;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MqTopicLoader {

    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("EAGLE_MQ_HOME is invalid!");
        }
        filePath = basePath + "/config/mq-topic.json";
        String fileContent = FileContentUtils.readFromFile(filePath);
        List<MqTopicModel> mqTopicModelList = JSON.parseArray(fileContent, MqTopicModel.class);
        CommonCache.setMqTopicModelList(mqTopicModelList);
    }

    public void startRefreshMqTopicInfoTask() {
        // 异步线程
        // 每隔15秒将内存中的配置刷新到磁盘里面
        CommonThreadPoolConfig.refreshMqTopicExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_MQ_TOPIC_TIME_STEP);
                        List<MqTopicModel> mqTopicModelList = CommonCache.getMqTopicModelList();
                        FileContentUtils.overWriteToFile(filePath, JSON.toJSONString(mqTopicModelList));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } while (true);
            }
        });
    }
}
