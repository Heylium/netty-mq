import com.helium.nettymq.broker.utils.MMapUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestMMapUtil {

    private MMapUtil mmapUtil;
    private static final String filePath = "C:/Programming/programming-works/github-projects/Java/eaglemq/nettymq/broker/commitlog/order_cancel_topic/00000000";

    private static final String consumeQueuePath = "C:/Programming/programming-works/github-projects/Java/eaglemq/nettymq/broker/commitlog/order_cancel_topic/0/00000000";

    @Before
    public void setUp() throws IOException {
        mmapUtil = new MMapUtil();
        mmapUtil.loadFileInMMap(filePath, 0, 1 * 1024 * 100);
        // mmapUtil.loadFileInMMap(consumeQueuePath, 0, 0.1 * 1024 * 1024);
        System.out.println("文件映射内存成功：0.1m");
    }

    @Test
    public void testLoadFile() throws IOException {
        // mmapUtil.loadFileInMMap(filePath, 0,100 * 1024 * 1024);
    }

    @Test
    public void testWriteAndReadFile() {
        String str = "this is a test content";
        byte[] content = str.getBytes();
        mmapUtil.writeContent(content);
        byte[] readContent = mmapUtil.readContent(0, content.length + 1);
        System.out.println(new String(readContent));
    }
}
