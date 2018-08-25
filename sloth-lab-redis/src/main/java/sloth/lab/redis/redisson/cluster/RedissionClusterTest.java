package sloth.lab.redis.redisson.cluster;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import sloth.lab.perf.Perf;
import sloth.lab.perf.data.StringData;

public class RedissionClusterTest {

    public static void main(String[] args) {
        Config config = new Config();
        config.useClusterServers().setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                // 可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://192.168.100.105:6380", "redis://192.168.100.105:6381") //
                .addNodeAddress("redis://192.168.100.105:6382", "redis://192.168.100.105:6383") //
                .addNodeAddress("redis://192.168.100.105:6384", "redis://192.168.100.105:6385");

        RedissonClient redisson = Redisson.create(config);

        perf(redisson);
    }

    public static void perf(RedissonClient redisson) {
        try {
            Perf perf = new Perf() {
                public TaskInThread buildTaskInThread() {
                    return new TaskInThread() {

                        public void initTask() throws Exception {
                        }

                        public void doTask() throws Exception {
                            redisson.getBucket(StringData.getRandomString(10)).get();
                        }
                    };
                }
            };
            perf.loopCount = 400000;
            perf.threadCount = 16;
            perf.logInterval = 20000;
            perf.run();
            perf.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisson.shutdown();
        }
    }
}
