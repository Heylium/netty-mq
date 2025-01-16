package com.helium.nettymq.broker.model;

import java.util.Map;

public class ConsumeQueueOffsetModel {

    private OffsetTable offsetTable;

    private class OffsetTable {
        private Map<String, ConsumerGroupDetail> topicConsumerGroupDetail;

        public Map<String, ConsumerGroupDetail> getTopicConsumerGroupDetail() {
            return topicConsumerGroupDetail;
        }

        public void setTopicConsumerGroupDetail(Map<String, ConsumerGroupDetail> topicConsumerGroupDetail) {
            this.topicConsumerGroupDetail = topicConsumerGroupDetail;
        }
    }

    private class ConsumerGroupDetail {
        private Map<String, Map<String, String>> consumerGroupDetailMap;

        public Map<String, Map<String, String>> getConsumerGroupDetailMap() {
            return consumerGroupDetailMap;
        }

        public void setConsumerGroupDetailMap(Map<String, Map<String, String>> consumerGroupDetailMap) {
            this.consumerGroupDetailMap = consumerGroupDetailMap;
        }
    }

    public OffsetTable getOffsetTable() {
        return offsetTable;
    }

    public void setOffsetTable(OffsetTable offsetTable) {
        this.offsetTable = offsetTable;
    }
}
