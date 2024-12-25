package com.helium.nettymq.broker.core;

import java.io.IOException;

public class MessageAppendHandler {

    private static String filePath = "C:\\Programming\\programming-works\\github-projects\\Java\\eaglemq\\nettymq\\broker\\commitlog\\order_cancel_topic\\00000000";
    public static String topicName = "order_cancel_topic";

    private MMapFileModelManager mMapFileModelManager = new MMapFileModelManager();

    public MessageAppendHandler() throws IOException {
        this.prepareMMapLoading();
    }

    private void prepareMMapLoading() throws IOException {
        MMapFileModel mapFileModel = new MMapFileModel();
        mapFileModel.loadFileInMMap(filePath, 0, 1 * 1024 * 1024);
        mMapFileModelManager.put(topicName, mapFileModel);
    }

    public void appendMsg(String topic, String content) {
        MMapFileModel mapFileModel = mMapFileModelManager.get(topicName);
        if (mapFileModel == null) {
            throw new RuntimeException("topic is invalid!");
        }
        mapFileModel.writeContent(content.getBytes());
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
        MessageAppendHandler messageAppendHandler = new MessageAppendHandler();
        messageAppendHandler.appendMsg(topicName, "this is content");
        messageAppendHandler.readMsg(topicName);
    }

}
