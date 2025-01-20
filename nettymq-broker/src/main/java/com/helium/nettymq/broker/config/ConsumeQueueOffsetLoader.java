package com.helium.nettymq.broker.config;

import com.alibaba.fastjson2.JSON;
import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.constants.BrokerConstants;
import com.helium.nettymq.broker.model.ConsumeQueueOffsetModel;
import com.helium.nettymq.broker.utils.FileContentUtils;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.TimeUnit;

public class ConsumeQueueOffsetLoader {
    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("MQ_HOME is invalid!");
        }
        filePath = basePath + "/config/consumequeue-offset.json";
        String fileContent = FileContentUtils.readFromFile(filePath);
        ConsumeQueueOffsetModel consumeQueueOffsetModel = JSON.parseObject(fileContent, ConsumeQueueOffsetModel.class);
        CommonCache.setConsumeQueueOffsetModel(consumeQueueOffsetModel);
    }

    public void startRefreshConsumeQueueOffsetTask() {
        //异步线程
        //每3秒将内存中的配置刷新到磁盘里面
        CommonThreadPoolConfig.refreshMqTopicExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_CONSUME_QUEUE_OFFSET_TIME_STEP);
                        System.out.println("consumequeue的offset写入情况刷新磁盘");
                        ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
                        FileContentUtils.overWriteToFile(filePath, JSON.toJSONString(consumeQueueOffsetModel));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } while (true);
            }
        });
    }
}
