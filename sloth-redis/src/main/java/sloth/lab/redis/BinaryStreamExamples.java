package sloth.lab.redis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.redisson.Redisson;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class BinaryStreamExamples {

    public static void main(String[] args) throws IOException {
        Config cfg = new Config();
        cfg.useClusterServers().setScanInterval(2000)
            .addNodeAddress("redis://192.168.100.105:6380", "redis://192.168.100.105:6381")
            .addNodeAddress("redis://192.168.100.105:6382", "redis://192.168.100.105:6383")
            .addNodeAddress("redis://192.168.100.105:6384", "redis://192.168.100.105:6385");

        RedissonClient redisson = Redisson.create(cfg);

        RBinaryStream stream = redisson.getBinaryStream("myStream");

        byte[] values = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        stream.trySet(values);
        stream.set(values);

        InputStream is = stream.getInputStream();
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = is.read()) != -1) {
            sb.append((char) ch);
        }
        String str = sb.toString();

        OutputStream os = stream.getOutputStream();
        for (int i = 0; i < values.length; i++) {
            byte c = values[i];
            os.write(c);
        }

        redisson.shutdown();
    }
}