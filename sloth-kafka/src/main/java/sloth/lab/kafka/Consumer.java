package sloth.lab.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class Consumer {

    @SuppressWarnings("resource")
    public static void consume(String brokers, String groupId) {
        // 配置consumer
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 订阅 'test' topic
        consumer.subscribe(Arrays.asList("test"));

        int count = 0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(200);
            if (records.count() == 0) {
                // timeout/nothing to read
            } else {
                for (ConsumerRecord<String, String> record : records) {
                    // Display record and count
                    count += 1;
                    System.out.println(count + ": " + record.value());
                }
            }
        }
    }
}
