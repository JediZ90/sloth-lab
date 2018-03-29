import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.kafka.connect.util.SafeObjectInputStream;

public class DataRead {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        SafeObjectInputStream is = null;
        try {
            is = new SafeObjectInputStream(new FileInputStream(new File("/Users/zhangbaohao/git/yanchuan2026/sloth-lab/sloth-cdc/src/main/java/sloth/lab/cdc/data/offset.dat")));

            Object obj = is.readObject();

            Map<byte[], byte[]> raw = (Map<byte[], byte[]>) obj;

            for (Map.Entry<byte[], byte[]> mapEntry : raw.entrySet()) {
                System.out.println(new String(mapEntry.getKey()));
                System.out.println(new String(mapEntry.getValue()));
            }
        } finally {
            if (is != null) {
                is.close();
                is = null;
            }
        }
    }
}
