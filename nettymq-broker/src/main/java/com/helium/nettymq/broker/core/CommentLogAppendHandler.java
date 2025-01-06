package com.helium.nettymq.broker.core;

import com.helium.nettymq.broker.constants.BrokerConstants;
import com.helium.nettymq.broker.model.CommitLogMessageModel;

import java.io.IOException;

public class CommentLogAppendHandler {

    private static String filePath = "C:\\Programming\\programming-works\\github-projects\\Java\\eaglemq\\nettymq\\broker\\commitlog\\order_cancel_topic\\00000000";
    public static String topicName = "order_cancel_topic";

    private MMapFileModelManager mMapFileModelManager = new MMapFileModelManager();


    public void prepareMMapLoading(String topicName) throws IOException {
        MMapFileModel mapFileModel = new MMapFileModel();
        mapFileModel.loadFileInMMap(topicName, 0, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        mMapFileModelManager.put(topicName, mapFileModel);
    }

    public void appendMsg(String topic, byte[] content) throws IOException {
        MMapFileModel mapFileModel = mMapFileModelManager.get(topic);
        if (mapFileModel == null) {
            throw new RuntimeException("topic is invalid!");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setSize(content.length);
        commitLogMessageModel.setContent(content);
        mapFileModel.writeContent(commitLogMessageModel);
    }

    public void readMsg(String topic) {
        MMapFileModel mapFileModel = mMapFileModelManager.get(topic);
        if (mapFileModel == null) {
            throw new RuntimeException("topic is invalid!");
        }
        byte[] content = mapFileModel.readContent(0, 10);
        System.out.println(new String(content));
    }

    public static void main(String[] args) throws IOException {
        CommentLogAppendHandler messageAppendHandler = new CommentLogAppendHandler();
        messageAppendHandler.appendMsg(topicName, "this is content".getBytes());
        messageAppendHandler.readMsg(topicName);
    }

}
