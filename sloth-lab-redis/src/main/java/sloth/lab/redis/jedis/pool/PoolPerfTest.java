package sloth.lab.redis.jedis.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sloth.lab.perf.Perf;

public class PoolPerfTest {

    public static void main(String[] args) {
        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
        jedisPoolConfig.setMaxTotal(8);
        jedisPoolConfig.setMaxIdle(8);
        // 不对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setMaxWaitMillis(30000);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "192.168.100.105", 6379, 2000, "redis@linux");

        try {
            Perf perf = new Perf() {
                public TaskInThread buildTaskInThread() {
                    return new TaskInThread() {

                        public void initTask() throws Exception {
                        }

                        public void doTask() throws Exception {
                            Jedis jedis = jedisPool.getResource();
                            jedis.get("testkey676K111");
                            jedis.close();
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
            jedisPool.close();
        }
    }
}
