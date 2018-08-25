package sloth.lab.redis.jedis;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class JedisExample {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.100.105", 6379);
        // 验证密码，如果没有设置密码这段代码省略
        jedis.auth("redis@linux");

        jedis.connect();// 连接

        Set<String> keys = jedis.keys("*"); // 列出所有的key
        System.out.println(keys);

        jedis.disconnect();// 断开连接

        jedis.close();
    }
}
