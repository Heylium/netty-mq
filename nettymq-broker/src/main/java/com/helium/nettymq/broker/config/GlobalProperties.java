package com.helium.nettymq.broker.config;

public class GlobalProperties {

    /**
     * 读取环境变量中配置的mq存储绝对路径地址
     */
    private String mqHome;

    public String getMqHome() {
        return mqHome;
    }

    public void setMqHome(String mqHome) {
        this.mqHome = mqHome;
    }
}
