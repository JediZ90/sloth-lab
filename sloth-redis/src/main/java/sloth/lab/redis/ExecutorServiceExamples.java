package sloth.lab.redis;

import java.util.Collections;
import java.util.concurrent.Callable;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RExecutorService;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

public class ExecutorServiceExamples {

    public static class RunnableTask implements Runnable {

        @RInject
        RedissonClient redisson;

        @Override
        public void run() {
            RMap<String, String> map = redisson.getMap("myMap");
            map.put("5", "11");
        }

    }

    public static class CallableTask implements Callable<String> {

        @RInject
        RedissonClient redisson;

        @Override
        public String call() throws Exception {
            RMap<String, String> map = redisson.getMap("myMap");
            map.put("1", "2");
            return map.get("3");
        }

    }

    public static void main(String[] args) {
        Config cfg = new Config();
        cfg.useClusterServers().setScanInterval(2000)
            .addNodeAddress("redis://192.168.100.105:6380", "redis://192.168.100.105:6381")
            .addNodeAddress("redis://192.168.100.105:6382", "redis://192.168.100.105:6383")
            .addNodeAddress("redis://192.168.100.105:6384", "redis://192.168.100.105:6385");

        RedissonClient redisson = Redisson.create(cfg);

        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(cfg);
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 1));
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();

        RExecutorService e = redisson.getExecutorService("myExecutor");
        e.execute(new RunnableTask());
        e.submit(new CallableTask());

        e.shutdown();
        node.shutdown();
    }

}