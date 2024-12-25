package com.helium.nettymq.broker.config;

public class GlobalProperties {

    /**
     * 读取环境变量中配置的mq存储绝对路径地址
     */
    private String nettyMqHome;

    public String getNettyMqHome() {
        return nettyMqHome;
    }

    public void setNettyMqHome(String nettyMqHome) {
        this.nettyMqHome = nettyMqHome;
    }
}
