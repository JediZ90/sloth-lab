package sloth.lab.cdc.binlog;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;

public class BinaryLogTest {

    /**
     * SHOW VARIABLES LIKE 'log_bin'; 
     * SHOW BINARY LOGS; 
     * SHOW BINLOG EVENTS;
     * 
     * 1 建立连接：https://dev.mysql.com/doc/internals/en/connection-phase-packets.html
     * https://dev.mysql.com/doc/internals/en/command-phase.html
     * 
     * oracle
     * http://tech.lede.com/2017/05/24/rd/server/databus/
     * https://docs.oracle.com/cd/E11882_01/server.112/e16545/xstrm_intro.htm#XSTRM72647
     * 
     * https://feisky.gitbooks.io/kubernetes/network/network.html
     * mysql hbase oracle
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        BinaryLogClient client = new BinaryLogClient("192.168.100.102", 3306, "paas_test_db", "root", "sloth@linux");
        client.registerEventListener(new EventListener() {

            @Override
            public void onEvent(Event event) {

                System.out.println(event.toString());
            }
        });
        client.setBinlogFilename("mysql-bin.000010");
        client.setBinlogPosition(1);

        client.connect();
    }
}
