package jugistanbul.validationservice;

import jugistanbul.entity.EventObject;
import jugistanbul.validationservice.kafka.consumer.StockCheckEventConsumer;
import jugistanbul.validationservice.kafka.producer.ValidationEventProducer;
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

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/

public class ValidationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private static final Consumer<Integer, EventObject> consumer = StockCheckEventConsumer.build();
    private static final Producer<Integer, EventObject> producer = ValidationEventProducer.build();

    public static void main(String[] args) {

        ConsumerRecords<Integer, EventObject> records;
        try {
            EventObject event;
            while (true) {
                records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<Integer, EventObject> record : records) {
                    LOGGER.info("Consumed record in validation service method: Key {} Value {} " +
                                    "Partition {} Offset {}", record.key(), record.value(),
                            record.partition(), record.offset());

                    event = record.value();
                    event.setNumberValid(isCardNumberValid(event.getCardNumber()));
                    event.setEvent("validation");
                    publishValidationEvent(event);
                }
                consumer.commitSync();
            }
        } catch (Exception ex) {
            LOGGER.error("An exception was thrown in validation service", ex);
        }
    }

    private static boolean isCardNumberValid(final String cardNumber) {
        try {
            Long.parseLong(cardNumber);
            return true;
        } catch (NumberFormatException ex) {
        }
        return false;
    }

    private static void publishValidationEvent(final EventObject event) {

        final ProducerRecord<Integer, EventObject> record =
                new ProducerRecord<>("VALIDATION_EVENT_TOPIC", event.getCustomerId(), event);

        try {
            producer.beginTransaction();
            final RecordMetadata metadata = producer.send(record).get();
            producer.commitTransaction();
            LOGGER.info("Validation event published to Kafka: Topic {} Partition {} Offset {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            LOGGER.error("An InterruptedException was thrown in publishValidationEvent method", e.getMessage());
        } catch (ExecutionException e) {
            LOGGER.error("An InterruptedException was thrown in publishValidationEvent method", e.getMessage());
        } catch (ProducerFencedException e) {
            LOGGER.error("An InterruptedException was thrown in publishValidationEvent method", e.getMessage());
            producer.close();
        } catch (KafkaException e) {
            LOGGER.error("A KafkaException was thrown in publishValidationEvent method", e.getMessage());
            producer.abortTransaction();
        }
    }
}
