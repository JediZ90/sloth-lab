package sloth.lab.kafka.streams;

import static java.lang.Thread.currentThread;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

public class FakedRunnable implements Runnable {

    private final KafkaStreams kafkaStreams;

    public FakedRunnable(final KafkaStreams kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }

    @Override
    public void run() {
        while (true) {
            ReadOnlyKeyValueStore<String, Object> view = kafkaStreams.store("queryable-weather-store", QueryableStoreTypes.keyValueStore());
            System.out.println((currentThread().getName()) + view.get("test"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}