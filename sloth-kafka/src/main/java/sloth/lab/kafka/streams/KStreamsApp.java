package sloth.lab.kafka.streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

public class KStreamsApp {

    public static void main(String[] args) throws InterruptedException {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "weather-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.100.107:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        builder.globalTable("whp.weather", Materialized.as("queryable-weather-store"));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);

        streams.start();

        ReadOnlyKeyValueStore<String, Object> view = streams.store("queryable-weather-store", QueryableStoreTypes.keyValueStore());
        System.out.println(view.all());
        new FakedRunnable(streams).run();

    }
}