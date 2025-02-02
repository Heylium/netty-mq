package com.helium.nettymq.broker.constants;

public class BrokerConstants {

    public static final String MQ_HOME = "MQ_HOME";
    public static final String BASE_STORE_PATH = "/commitlog/";
    public static final Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1 * 1024 * 1024;
    public static final Integer DEFAULT_REFRESH_MQ_TOPIC_TIME_STEP = 3;
    public static final Integer DEFAULT_REFRESH_CONSUME_QUEUE_OFFSET_TIME_STEP = 1;
}
