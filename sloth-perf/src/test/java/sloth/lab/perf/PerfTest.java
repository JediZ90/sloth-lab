package sloth.lab.perf;

public class PerfTest {

    public static void main(String[] args) {
        try {
            Perf perf = new Perf() {

                public TaskInThread buildTaskInThread() {
                    return new TaskInThread() {

                        public void initTask() throws Exception {
                        }

                        public void doTask() throws Exception {
                        }
                    };
                }
            };
            perf.loopCount = 10000000;
            perf.threadCount = 16;
            perf.logInterval = 100000;
            perf.run();
            perf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
