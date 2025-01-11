package com.helium.nettymq.broker.model;

import com.helium.nettymq.broker.utils.ByteConvertUtils;

/**
 * commitLog真实数据存储对象模型
 */
public class CommitLogMessageModel {

    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] convertToBytes() {
        return this.getContent();
    }
}
