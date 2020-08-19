package jugistanbul.orderservice.kafka.event.stockcheck.producer;

import jugistanbul.entity.EventObject;
import jugistanbul.serializer.CustomSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Properties;
import java.util.UUID;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/
@ApplicationScoped
public class OrderEventProducer
{
    Producer<Integer, EventObject> producer;

    @Inject
    private Logger logger;

    @PostConstruct
    public void initProducer(){

        logger.info("Initialize order event producer...");

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());

        producer = new KafkaProducer<>(props);
        producer.initTransactions();
    }

    @Produces
    public Producer<Integer, EventObject> getProducer() {
        return producer;
    }
}
