package sloth.lab.redis.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import sloth.lab.redis.jedis.pool.JedisPoolManager;

public class RedisReadTaskSchuder {

    private static Logger logger = LoggerFactory.getLogger(RedisReadTaskSchuder.class);

    // 消息计数器
    AtomicLong counter = new AtomicLong(0);
    // 定时器
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    volatile boolean runState = true;
    // 开启时间
    long startTime = 0;

    // 线程数量
    int threadNumber = 0;
    // 工作线程
    Thread[] workers = null;

    public RedisReadTaskSchuder(int threadNumber) {
        // 默认线程数量为硬件内核数的2倍
        this.threadNumber = threadNumber;
        workers = new Thread[threadNumber];

        for (int i = 0; i < threadNumber; i++) {
            workers[i] = new Thread(new RedisReadTask(JedisPoolManager.instance().getJedis()));
            workers[i].setDaemon(true);
            workers[i].setName(ConfigManager.read_thread_name + "i");
        }

        executorService.scheduleAtFixedRate(new PrintTimer(), 2, 15, TimeUnit.SECONDS);
    }

    /**
     * 启动工作线程
     */
    public void start() {
        for (int i = 0; i < threadNumber; i++) {
            workers[i].start();
        }
        startTime = System.currentTimeMillis();
    }

    /**
     * 计数器重置
     * 
     */
    public void resetCount() {
        this.counter.set(0);
        startTime = System.currentTimeMillis();
    }

    public long getCount() {
        return this.counter.get();
    }

    /**
     * 关闭线程
     */
    public void shutdown() {
        runState = false;
        executorService.shutdown();
    }

    public void printReuslt() {
        logger.info("---获取到数据数量:--" + counter.get());
    }

    class RedisReadTask implements Runnable {

        private Jedis jedis = null;

        RedisReadTask(Jedis jedis) {
            this.jedis = jedis;
        }

        @Override
        public void run() {

            while (runState) {
                try {
                    jedis.set(Main.testKey.getBytes(), Main.testKey.getBytes());
                    
                    byte[] str = jedis.get(Main.testKey.getBytes());
                    if (null != str) {
                        counter.incrementAndGet();
                    }
                } catch (Throwable t) {
                    logger.error("", t);
                    // 连接失败
                    if (!jedis.isConnected()) {
                        // 返回连接池里面
                        jedis.close();
                        // 重新获取连接
                        jedis = JedisPoolManager.instance().getJedis();
                    }
                }
            }
            // 返回去jedis pool 里面
            jedis.close();
        }
    }

    class PrintTimer implements Runnable {

        @Override
        public void run() {

            try {
                StringBuilder sb = new StringBuilder();

                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                long _count = counter.get();
                long _endTime = System.currentTimeMillis();

                long throughput_s = (_count * 1000) / (_endTime - startTime);
                long minTime = (_endTime - startTime) / (1000 * 60);
                long hourTime = (_endTime - startTime) / (1000 * 60 * 60);

                long throughput_m = (minTime != 0) ? _count / minTime : 0;
                long throughput_h = (hourTime != 0) ? _count / hourTime : 0;

                sb.append("\n======================================================\n");
                sb.append("---------开始时间--------------结束时间-------------获取条数-----每秒吞吐量-----分钟吞吐量-----小时吞吐量-----测试运行线程数量----每个消息的大小\n");

                sb.append("-");
                sb.append(format.format(new Date(startTime)));
                sb.append("---");
                sb.append(format.format(new Date()));
                sb.append("------");
                sb.append(counter.get());
                sb.append("------");
                sb.append(throughput_s);
                sb.append("---------");
                sb.append(throughput_m);
                sb.append("---------");
                sb.append(throughput_h);
                sb.append("-----------");
                sb.append(threadNumber);
                sb.append("-----------");
                sb.append("672byte");
                sb.append("-----");

                logger.error(sb.toString());
                logger.error("\n");

            } catch (Throwable t) {
                logger.error("", t);
            }
        }
    }
}
