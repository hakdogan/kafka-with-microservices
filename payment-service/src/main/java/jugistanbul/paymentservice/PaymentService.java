package jugistanbul.paymentservice;

import jugistanbul.entity.EventObject;
import jugistanbul.paymentservice.kafka.consumer.ValidationEventConsumer;
import jugistanbul.paymentservice.kafka.producer.BillingEventProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private static final Consumer<Integer, EventObject> consumer = ValidationEventConsumer.build();
    private static final Producer<Integer, EventObject> producer = BillingEventProducer.build();

    public static void main(String[] args) {
        ConsumerRecords<Integer, EventObject> records;
        try {
            EventObject event;
            while (true) {
                records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<Integer, EventObject> record : records) {
                    LOGGER.info("Consumed record in payment service method: Key {} Value {} " +
                                    "Partition {} Offset {}", record.key(), record.value(),
                            record.partition(), record.offset());

                    event = record.value();
                    if (event.isNumberValid()) {
                        event.setEvent("billing");
                        event.setPrice(ThreadLocalRandom.current().nextInt(100, 500) * event.getAmount());
                        publishBillingEvent(event);
                    }
                }
                consumer.commitSync();
            }
        } catch (Exception ex) {
            LOGGER.error("An exception was thrown in validation service", ex);
        }
    }

    private static void publishBillingEvent(final EventObject event) {

        final ProducerRecord<Integer, EventObject> record =
                new ProducerRecord<>("BILLING_EVENT_TOPIC", event.getCustomerId(), event);

        try {
            producer.beginTransaction();
            final RecordMetadata metadata = producer.send(record).get();
            producer.commitTransaction();
            LOGGER.info("Billing event published to Kafka: Topic {} Partition {} Offset {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            LOGGER.error("An InterruptedException was thrown in publishBillingEvent method", e.getMessage());
        } catch (ExecutionException e) {
            LOGGER.error("An InterruptedException was thrown in publishBillingEvent method", e.getMessage());
        } catch (ProducerFencedException e) {
            LOGGER.error("An InterruptedException was thrown in publishBillingEvent method", e.getMessage());
            producer.close();
        } catch (KafkaException e) {
            LOGGER.error("A KafkaException was thrown in publishBillingEvent method", e.getMessage());
            producer.abortTransaction();
        }
    }
}
