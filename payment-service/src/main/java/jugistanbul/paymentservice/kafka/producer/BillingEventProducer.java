package jugistanbul.paymentservice.kafka.producer;

import jugistanbul.entity.EventObject;
import jugistanbul.serializer.CustomSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 19.08.2020
 **/

public class BillingEventProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingEventProducer.class);

    private BillingEventProducer() {}

    public static Producer<Integer, EventObject> build(){

        LOGGER.info("Initialize billing event producer...");

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());

        final Producer<Integer, EventObject> producer = new KafkaProducer<>(props);
        producer.initTransactions();
        return producer;
    }
}
