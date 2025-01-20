package com.helium.nettymq.broker;


import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.config.ConsumeQueueOffsetLoader;
import com.helium.nettymq.broker.config.GlobalPropertiesLoader;
import com.helium.nettymq.broker.config.MqTopicLoader;
import com.helium.nettymq.broker.core.CommentLogAppendHandler;
import com.helium.nettymq.broker.model.MqTopicModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BrokerStartUp {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static MqTopicLoader mqTopicLoader;
    private static CommentLogAppendHandler commitLogAppendHandler;
    private static ConsumeQueueOffsetLoader consumeQueueOffsetLoader;

    /**
     * 初始化配置逻辑
     * @throws IOException
     */
    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        mqTopicLoader = new MqTopicLoader();
        mqTopicLoader.loadProperties();
        mqTopicLoader.startRefreshMqTopicInfoTask();
        consumeQueueOffsetLoader = new ConsumeQueueOffsetLoader();
        consumeQueueOffsetLoader.loadProperties();
        consumeQueueOffsetLoader.startRefreshConsumeQueueOffsetTask();
        commitLogAppendHandler = new CommentLogAppendHandler();

        for (MqTopicModel mqTopicModel : CommonCache.getMqTopicModelMap().values()) {
            String topicName = mqTopicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //加载配置 ，缓存对象的生成
        initProperties();
        //模拟初始化文件映射
        String topic = "order_cancel_topic";
        for (int i = 0; i < 50000; i++) {
            commitLogAppendHandler.appendMsg(topic, ("this is content " + i).getBytes());
            TimeUnit.MILLISECONDS.sleep(1);
        }
        // commitLogAppendHandler.readMsg(topic);
    }

}