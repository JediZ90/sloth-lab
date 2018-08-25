package sloth.lab.cdc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.connect.source.SourceRecord;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.embedded.EmbeddedEngine.ConnectorCallback;

public class Application {

    public static void main(String[] args) throws InterruptedException {
       
        BlockingQueue<SourceRecord> consumedLines = new ArrayBlockingQueue<>(100);;
        
        // Define the configuration for the embedded and MySQL connector ...
        Configuration config = Configuration.create()
                                            .with("name", "mysql-connector")
                                            /* begin engine properties */
                                            .with("connector.class",
                                                  "io.debezium.connector.mysql.MySqlConnector")
                                            
                                            .with("offset.storage",
                                                  "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                                            .with("offset.storage.file.filename",
                                                  "/Users/zhangbaohao/git/yanchuan2026/slothlab/src/main/java/sloth/lab/cdc/data/offset.dat")
                                            .with("offset.flush.interval.ms", 60000)
                                            
                                            /* begin connector properties */
                                            .with("database.hostname", "192.168.100.102")
                                            .with("database.port", 3306)
                                            .with("database.user", "root")
                                            .with("database.password", "sloth@linux")
                                            .with("server.id", 85744)
                                            .with("database.server.name", "my-app-connector")
                                            .with("database.history",
                                                  "io.debezium.relational.history.FileDatabaseHistory")
                                            .with("database.history.file.filename",
                                                  "/Users/zhangbaohao/git/yanchuan2026/slothlab/src/main/java/sloth/lab/cdc/data/dbhistory.dat")

                                            .build();
        
        ConnectorCallback connectorCallback = new ConnectorCallback() {
            public void connectorStarted() {
                // nothing by default
                System.out.println("cus::connectorStarted");
            }

            public void connectorStopped() {
                // nothing by default
                System.out.println("cus::connectorStopped");
            }
            
            public void taskStarted() {
                // nothing by default
                System.out.println("cus::taskStarted");
            }

            public void taskStopped() {
                // nothing by default
                System.out.println("cus::taskStopped");
            }
        };
        
        // Create the engine with this configuration ...
        EmbeddedEngine engine = EmbeddedEngine.create()
                                              .using(config)
                                              .using(connectorCallback)
                                              .notifying((record) -> {
                                                  try {
                                                      System.out.println(record.toString());
                                                      consumedLines.put(record);
                                                  } catch (InterruptedException e) {
                                                      e.printStackTrace();
                                                      Thread.interrupted();
                                                  }
                                              })
                                              .build();
        
        ExecutorService executor = Executors.newFixedThreadPool(1);
        
        executor.execute(engine);
    }
}
