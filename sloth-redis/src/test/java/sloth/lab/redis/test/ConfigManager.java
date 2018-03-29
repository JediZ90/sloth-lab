package sloth.lab.redis.test;

public class ConfigManager {

    public static final String redis_host = "192.168.100.105";
    public static final int redis_port = 6379;
    public static final int redis_pool_timeOut = 10000;
    public static final String redis_auth = "redis@linux";
    public static final boolean redis_pool_database = false;

    public static final int redis_pool_maxTotal = 500;
    public static final int redis_pool_maxIdle = 8;
    public static final long redis_pool_maxWait = 30000;

    public static final int read_thread_number = 4;
    public static final String read_thread_name = "redis_read_thread";

}
