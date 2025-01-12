package com.helium.nettymq.broker.config;

import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.constants.BrokerConstants;
import io.netty.util.internal.StringUtil;

public class GlobalPropertiesLoader {

    public void loadProperties() {
        GlobalProperties globalProperties = new GlobalProperties();
        // String mqHome = System.getProperty(BrokerConstants.MQ_HOME);
        // String mqHome = System.getenv(BrokerConstants.MQ_HOME);
        String mqHome = "C:/Programming/programming-works/github-projects/Java/eaglemq/nettymq/broker";
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("mq home is null");
        }
        globalProperties.setMqHome(mqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
