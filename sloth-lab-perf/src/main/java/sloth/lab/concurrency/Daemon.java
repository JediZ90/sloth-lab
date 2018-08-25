package sloth.lab.concurrency;

public class Daemon {

    /**
     * 注：构建Daemon线程时，不能依靠finally块中的内容来确保执行关闭或倾力资源的逻辑
     * @param args
     */
    public static void main(String[] args) {
        Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
        thread.setDaemon(true);
        thread.start();
    }

    static class DaemonRunner implements Runnable {
        @Override
        public void run() {
            try {

                SleepUtils.second(10);
            } finally {
                System.out.println("DaemonThread finally run.");
            }
        }
    }
}
