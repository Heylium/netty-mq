package com.helium.nettymq.broker.model;

public class ConsumerQueueDetailModel {

    private int commitLogIndex;

    private long msgIndex;

    private int msgLength;

    public int getCommitLogIndex() {
        return commitLogIndex;
    }

    public void setCommitLogIndex(int commitLogIndex) {
        this.commitLogIndex = commitLogIndex;
    }

    public long getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(long msgIndex) {
        this.msgIndex = msgIndex;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }
}
