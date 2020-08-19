package jugistanbul.validationservice.kafka.consumer;

import jugistanbul.deserializer.CustomDeserializer;
import jugistanbul.entity.EventObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Properties;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 19.08.2020
 **/

public class StockCheckEventConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StockCheckEventConsumer.class);

    private StockCheckEventConsumer() {}

    public static Consumer<Integer, EventObject> build() {

        LOGGER.info("Initialize stock check event consumer...");

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stockCheckEventConsumerGroup02");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Consumer<Integer, EventObject> consumer = new KafkaConsumer<>(props,
                new IntegerDeserializer(),
                new CustomDeserializer<EventObject>(EventObject.class));

        consumer.subscribe(Collections.singletonList("STOCK_CHECK_EVENT_TOPIC"));
        return consumer;
    }
}
