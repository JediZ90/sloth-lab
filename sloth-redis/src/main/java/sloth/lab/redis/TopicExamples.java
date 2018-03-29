package sloth.lab.redis;

import java.util.concurrent.CountDownLatch;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;

public class TopicExamples {

    public static void main(String[] args) throws InterruptedException {
        Config cfg = new Config();
        cfg.useClusterServers().setScanInterval(2000)
            .addNodeAddress("redis://192.168.100.105:6380", "redis://192.168.100.105:6381")
            .addNodeAddress("redis://192.168.100.105:6382", "redis://192.168.100.105:6383")
            .addNodeAddress("redis://192.168.100.105:6384", "redis://192.168.100.105:6385");

        RedissonClient redisson = Redisson.create(cfg);

        CountDownLatch latch = new CountDownLatch(1);

        RTopic<String> topic = redisson.getTopic("topic2");
        topic.addListener(new MessageListener<String>() {
            @Override
            public void onMessage(String channel, String msg) {
                latch.countDown();
            }
        });

        topic.publish("msg");
        latch.await();

        redisson.shutdown();
    }

}