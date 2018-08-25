package sloth.lab.redis.jedis.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sloth.lab.redis.test.ConfigManager;

public class JedisPoolManager {

    private static Logger logger = LoggerFactory.getLogger(JedisPoolManager.class);

    private static JedisPoolManager instance = null;
    private JedisPool jedisPool = null;

    private JedisPoolManager() {
    }

    public static synchronized JedisPoolManager instance() {
        if (null == instance) {
            instance = new JedisPoolManager();
            instance.init();
        }
        return instance;
    }

    public void init() {
        logger.info("------init redis start------");

        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();

        jedisPoolConfig.setMaxTotal(ConfigManager.redis_pool_maxTotal);
        jedisPoolConfig.setMaxIdle(ConfigManager.redis_pool_maxIdle);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setMaxWaitMillis(ConfigManager.redis_pool_maxWait);

        jedisPool = new JedisPool(jedisPoolConfig, ConfigManager.redis_host, ConfigManager.redis_port, ConfigManager.redis_pool_timeOut, ConfigManager.redis_auth, ConfigManager.redis_pool_database);

        logger.info("------init redis end------");
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void rebackPool(Jedis jedis) {
        jedis.close();
    }

    public String getString(String key) {
        Jedis jedis = getJedis();
        String result = jedis.get(key);
        this.rebackPool(jedis);
        return result;
    }

    public void set(String key, String value) {
        Jedis jedis = getJedis();
        jedis.set(key, value);
        this.rebackPool(jedis);
    }

    public void shutdown() {
        logger.info("---redis close--" + jedisPool.getNumActive());
        jedisPool.close();
    }
}