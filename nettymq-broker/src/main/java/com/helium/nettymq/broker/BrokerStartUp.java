package com.helium.nettymq.broker;


import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.config.GlobalPropertiesLoader;
import com.helium.nettymq.broker.config.MqTopicLoader;
import com.helium.nettymq.broker.core.CommentLogAppendHandler;
import com.helium.nettymq.broker.model.MqTopicModel;

import java.io.IOException;

public class BrokerStartUp {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static MqTopicLoader mqTopicLoader;
    private static CommentLogAppendHandler messageAppendHandler;

    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        mqTopicLoader = new MqTopicLoader();
        mqTopicLoader.loadProperties();
        messageAppendHandler = new CommentLogAppendHandler();

        for (MqTopicModel mqTopicModel : CommonCache.getMqTopicModelMap().values()) {
            String topicName = mqTopicModel.getTopic();
            messageAppendHandler.prepareMMapLoading(topicName);;
        }
    }

    public static void main(String[] args) throws IOException {
        //加载配置 ，缓存对象的生成
        initProperties();
        //模拟初始化文件映射
        String topic = "order_cancel_topic";
        messageAppendHandler.appendMsg(topic, "this is a test content");
        messageAppendHandler.readMsg(topic);
    }

}