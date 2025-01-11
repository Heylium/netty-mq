package com.helium.nettymq.broker.model;

public class ConsumerQueueDetailModel {

    private String commitLogFileName;

    private long msgIndex;

    private int msgLength;

    public String getCommitLogFileName() {
        return commitLogFileName;
    }

    public void setCommitLogFileName(String commitLogFileName) {
        this.commitLogFileName = commitLogFileName;
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
