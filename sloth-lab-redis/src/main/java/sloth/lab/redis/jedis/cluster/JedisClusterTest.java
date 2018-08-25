package sloth.lab.redis.jedis.cluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import sloth.lab.perf.Perf;
import sloth.lab.perf.data.StringData;

public class JedisClusterTest {

    public static void main(String[] args) {
        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
        jedisPoolConfig.setMaxTotal(8);
        jedisPoolConfig.setMaxIdle(8);
        // 不对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setMaxWaitMillis(30000);

        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort("192.168.100.105", 6380));
        jedisClusterNodes.add(new HostAndPort("192.168.100.105", 6381));
        jedisClusterNodes.add(new HostAndPort("192.168.100.105", 6382));
        jedisClusterNodes.add(new HostAndPort("192.168.100.105", 6383));
        jedisClusterNodes.add(new HostAndPort("192.168.100.105", 6384));
        jedisClusterNodes.add(new HostAndPort("192.168.100.105", 6385));
        JedisCluster jc = new JedisCluster(jedisClusterNodes, jedisPoolConfig);

        System.out.println(jc.zadd("keySet", System.currentTimeMillis(), "test1"));
        
        //perf(jc);
    }

    public static void perf(JedisCluster jc) {
        try {
            Perf perf = new Perf() {
                public TaskInThread buildTaskInThread() {
                    return new TaskInThread() {

                        public void initTask() throws Exception {
                        }

                        public void doTask() throws Exception {
                            jc.get(StringData.getRandomString(10));
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
            try {
                jc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
