package com.helium.nettymq.broker;


import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.config.GlobalPropertiesLoader;
import com.helium.nettymq.broker.config.MqTopicLoader;
import com.helium.nettymq.broker.constants.BrokerConstants;
import com.helium.nettymq.broker.core.MessageAppendHandler;
import com.helium.nettymq.broker.model.MqTopicModel;

import java.io.IOException;
import java.util.List;

public class BrokerStartUp {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static MqTopicLoader mqTopicLoader;
    private static MessageAppendHandler messageAppendHandler;

    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        mqTopicLoader = new MqTopicLoader();
        mqTopicLoader.loadProperties();
        messageAppendHandler = new MessageAppendHandler();

        List<MqTopicModel> mqTopicModelList = CommonCache.getMqTopicModelList();
        for (MqTopicModel mqTopicModel : mqTopicModelList) {
            String topicName = mqTopicModel.getTopic();
            String filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BrokerConstants.BASE_STORE_PATH
                    + topicName
                    + "/00000001";
            messageAppendHandler.prepareMMapLoading();;
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