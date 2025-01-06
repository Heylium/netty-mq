package com.helium.nettymq.broker.model;

import java.util.concurrent.atomic.AtomicLong;

public class CommitLogModel {

    private String fileName;

    private Long offsetLimit;

    private AtomicLong offset;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(Long offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    public AtomicLong getOffset() {
        return offset;
    }

    public void setOffset(AtomicLong offset) {
        this.offset = offset;
    }

    public Long countDiff() {
        return this.offsetLimit - this.offset.get();
    }

    @Override
    public String toString() {
        return "CommitLogModel{" +
                "fileName='" + fileName + '\'' +
                ", offsetLimit=" + offsetLimit +
                ", offset=" + offset +
                '}';
    }
}
