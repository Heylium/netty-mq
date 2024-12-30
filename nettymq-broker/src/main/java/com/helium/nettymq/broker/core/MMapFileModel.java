package com.helium.nettymq.broker.core;

import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.constants.BrokerConstants;
import com.helium.nettymq.broker.model.CommitLogModel;
import com.helium.nettymq.broker.model.MqTopicModel;
import com.helium.nettymq.broker.utils.CommitLogFileNameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础mmap对象模型
 */
public class MMapFileModel {

    private static Map<String, MMapFileModel> mMapFileModels = new HashMap<>();

    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;
    private String topic;

    /**
     * 指定offset做文件的映射
     *
     * @param topicName 文件路径
     * @param startOffset 开始映射的offset
     * @param mappedSize 映射的文件大小
     */
    public void loadFileInMMap(String topicName, int startOffset, int mappedSize) throws IOException {
        String filePath = getLatestCommitLogFile(topicName);
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + " inValid");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    /**
     * 获取最新的commitLog文件路径
     * @param topicName
     * @return
     */
    private String getLatestCommitLogFile(String topicName) {
        MqTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topicName);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic is inValid! topicName is " + topicName);
        }
        CommitLogModel commitLogModel = mqTopicModel.getCommitLogModel();
        long diff = commitLogModel.getOffsetLimit() - commitLogModel.getOffset();
        String filePath = null;
        if (diff == 0) { //已经写满了
            filePath = this.createNewCommitLogFile(topicName, commitLogModel);
        } else if (diff > 0) { //还有机会写入
            filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BrokerConstants.BASE_STORE_PATH
                    + topicName
                    + commitLogModel.getFileName();
        }
        return filePath;
    }

    private String createNewCommitLogFile(String topicName, CommitLogModel commitLogModel) {
        String newFileName = CommitLogFileNameUtil.incrCommitLogFileName(commitLogModel.getFileName());
        String newFilePath = CommonCache.getGlobalProperties().getMqHome()
                + BrokerConstants.BASE_STORE_PATH
                + topicName
                + newFileName;
        File newCommitLogFile = new File(newFilePath);
        try {
            newCommitLogFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFilePath;
    }

    /**
     * 从文件的指定offset开始读取内容
     *
     * @param readOffset
     * @param size
     * @return
     */
    public byte[] readContent(int readOffset, int size) {
        mappedByteBuffer.position(readOffset);
        byte[] content = new byte[size];
        // 从内存空间读取数据
        int j = 0;
        for (int i = 0; i < size; i++) {
            byte b = mappedByteBuffer.get(readOffset + i);
            content[j++] = b;
        }
        return content;
    }

    /**
     * 更高性能的写入api
     * @param content
     */
    public void writeContent(byte[] content) {
        this.writeContent(content, false);
    }

    /**
     * 写入数据到磁盘当中
     *
     * @param content
     * @param force
     */
    public void writeContent(byte[] content, boolean force) {
        // 默认刷到page cache中，
        // 如果需要强制刷盘，需要兼容
        mappedByteBuffer.put(content);
        // 强制刷盘
        if (force) {
            mappedByteBuffer.force();
        }
    }

    public void clear() throws NoSuchMethodException {

        // 在关闭资源时执行以下代码释放内存
        // 不推荐的原因是因为使用了sun包下不稳定的代码
        // Method m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
        // m.setAccessible(true);
        // m.invoke(FileChannelImpl.class, mappedByteBuffer);
    }

    /**
     * 释放mmap内存占用
     */
    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) {
            return;
        }
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private Method method(Object target, String methodName, Class<?>[] args) throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }
        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null) {
            return buffer;
        } else {
            return viewed(viewedBuffer);
        }
    }
}
