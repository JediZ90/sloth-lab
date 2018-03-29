package sloth.lab.redis;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.redisson.Redisson;
import org.redisson.api.RBatch;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class Test {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Config cfg = new Config();
        cfg.useClusterServers().setScanInterval(1000) // 对Redis集群节点状态扫描的时间间隔。单位是毫秒。
                .addNodeAddress("redis://192.168.100.105:6380", "redis://192.168.100.105:6381") //
                .addNodeAddress("redis://192.168.100.105:6382", "redis://192.168.100.105:6383") //
                .addNodeAddress("redis://192.168.100.105:6384", "redis://192.168.100.105:6385");

        RedissonClient redisson = Redisson.create(cfg);

        RBucketAsync<String> keyObject = redisson.getBucket("key");

        System.out.println("1");

        RFuture<String> future = keyObject.getAsync();

        System.out.println(future.get());

        
        RBatch batch = redisson.createBatch();
        batch.getBucket("key");
        
        
        redisson.shutdown();
    }
}
