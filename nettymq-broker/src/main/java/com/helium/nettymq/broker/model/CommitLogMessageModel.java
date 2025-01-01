package com.helium.nettymq.broker.model;

import com.helium.nettymq.broker.utils.ByteConvertUtils;

/**
 * commitLog真实数据存储对象模型
 */
public class CommitLogMessageModel {

    private int size;

    private byte[] content;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] convertToBytes() {
        byte[] sizeBytes = ByteConvertUtils.intToBytes(this.getSize());
        byte[] content = this.getContent();
        byte[] mergeResultByte = new byte[sizeBytes.length + content.length];
        int j = 0;
        for (int i = 0; i < sizeBytes.length; i++, j++) {
            mergeResultByte[j] = sizeBytes[i];
        }
        for (int i = 0; i < content.length; i++, j++) {
            mergeResultByte[j] = content[i];
        }
        return mergeResultByte;
    }
}
