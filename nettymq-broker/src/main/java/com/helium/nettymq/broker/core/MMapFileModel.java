package com.helium.nettymq.broker.core;

import com.helium.nettymq.broker.cache.CommonCache;
import com.helium.nettymq.broker.constants.BrokerConstants;
import com.helium.nettymq.broker.model.CommitLogMessageModel;
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
        this.topic = topicName;
        String filePath = getLatestCommitLogFile(topicName);
        this.doMMap(filePath, startOffset, mappedSize);
    }

    /**
     * 执行mmap步骤
     * @param filePath
     * @param startOffset
     * @param mappedSize
     * @throws IOException
     */
    private void doMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + " inValid");
        }
        this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
        this.mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
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
        long diff = commitLogModel.countDiff();
        String filePath = null;
        if (diff == 0) { //已经写满了
            filePath = this.createNewCommitLogFile(topicName, commitLogModel);
        } else if (diff > 0) { //还有机会写入
            filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BrokerConstants.BASE_STORE_PATH
                    + topicName
                    + "/"
                    + commitLogModel.getFileName();
        }
        return filePath;
    }

    private String createNewCommitLogFile(String topicName, CommitLogModel commitLogModel) {
        String newFileName = CommitLogFileNameUtil.incrCommitLogFileName(commitLogModel.getFileName());
        String newFilePath = CommonCache.getGlobalProperties().getMqHome()
                + BrokerConstants.BASE_STORE_PATH
                + topicName
                + "/"
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
     * @param commitLogMessageModel
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel) throws IOException {
        this.writeContent(commitLogMessageModel, false);
    }

    /**
     * 写入数据到磁盘当中
     *
     * @param commitLogMessageModel
     * @param force
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel, boolean force) throws IOException {
        //offset会用一个原子类AtomicLong去管理
        //线程安全问题：线程1：111，线程2：122
        //加锁机制 （锁的选择非常重要）
        MqTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("mqTopicModel is null");
        }
        CommitLogModel commitLogModel = mqTopicModel.getCommitLogModel();
        if (commitLogModel == null) {
            throw new IllegalArgumentException("commitLogModel is null");
        }
        this.checkCommitLogHasEnableSpace(commitLogMessageModel);

        // 默认刷到page cache中，
        // 如果需要强制刷盘，需要兼容
        mappedByteBuffer.put(commitLogMessageModel.convertToBytes());
        commitLogModel.getOffset().addAndGet(commitLogMessageModel.getSize());
        // 强制刷盘
        if (force) {
            mappedByteBuffer.force();
        }
    }

    private void checkCommitLogHasEnableSpace(CommitLogMessageModel commitLogMessageModel) throws IOException {
        MqTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        CommitLogModel commitLogModel = mqTopicModel.getCommitLogModel();
        long writeAbleOffsetNum = commitLogModel.countDiff();
        //空间不足，需要创建新的commitLog文件并且做映射
        if (!(writeAbleOffsetNum >= commitLogMessageModel.getSize())) {
            //00000000文件 -》00000001文件
            //commitLog剩余150byte大小的空间，最新的消息体积是151byte
            String newCommitLogPath = this.createNewCommitLogFile(topic, commitLogModel);
            // 新文件路径映射进来
            this.doMMap(newCommitLogPath, 0, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
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
