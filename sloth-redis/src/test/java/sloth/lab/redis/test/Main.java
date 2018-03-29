package sloth.lab.redis.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sloth.lab.redis.jedis.pool.JedisPoolManager;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    static String testStr = "{.....}";
    static String testKey = "testkey676K";

    // 执行时间
    static int exetime = 1;

    public static void main(String[] args) throws InterruptedException {

        logger.info("----start-testServer----start--");

        // 初始化redis连接池
        JedisPoolManager.instance();
        // 创建线程调度
        RedisReadTaskSchuder schuder = new RedisReadTaskSchuder(ConfigManager.read_thread_number);
        // 线程启动
        schuder.start();

        logger.info("----start-testServer----end--");
    }

    public void testNative() throws InterruptedException {
        // 初始化redis连接池
        JedisPoolManager.instance();
        // 创建线程调度
        RedisReadTaskSchuder schuder = new RedisReadTaskSchuder(ConfigManager.read_thread_number);
        // 线程启动
        schuder.start();
        // 预热一秒
        Thread.sleep(1000);
        // 计数器重置
        schuder.resetCount();
        // 执行五秒
        Thread.sleep(60000 * exetime);
        schuder.shutdown();
        JedisPoolManager.instance().shutdown();

        schuder.printReuslt();

        logger.info("" + schuder.getCount() / (60 * exetime));
    }
}
