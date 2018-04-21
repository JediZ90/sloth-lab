package sloth.lab.redis.jedis.sentinel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import sloth.lab.perf.util.StreamUtil;

public class JedisSentinelPoolTest {

    public static void main(String[] args) {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("192.168.100.105:26301");
        sentinels.add("192.168.100.105:26302");
        sentinels.add("192.168.100.105:26303");

        String masterName = "mymaster";

        JedisSentinelPool redisSentinelJedisPool = new JedisSentinelPool(masterName, sentinels);

        Jedis jedis = null;
        try {
            jedis = redisSentinelJedisPool.getResource();

            InputStream in = JedisSentinelPoolTest.class.getClassLoader().getResourceAsStream("sloth/lab/redis/lua/saveConfig.lua");
            String saveConfigLua = StreamUtil.readAsString(in, "UTF-8");
            /**
             local moduleVersionUnit = KEYS[1]
            local documentVersion = KEYS[2]
            local document = KEYS[3]
            local mdptInSize = KEYS[4]
            local mdptOutSize = KEYS[5]
             */

            String moduleVersionUnit = "TestB_1.0.0_1";
            
            List<String> keys = new ArrayList<>();
            keys.add("TestB_1.0.0_1");
            keys.add("103");
            keys.add("document");
            keys.add("1");
            keys.add("1");
            keys.add("TestB_1.0.0_2");
            keys.add("TestB_1.0.0_2");
            System.out.println(jedis.eval("return redis.call('HGETALL', 'mcv')", keys, new ArrayList<>()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        redisSentinelJedisPool.close();

    }
}
