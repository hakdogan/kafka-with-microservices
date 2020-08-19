package jugistanbul.stockservice;

import jugistanbul.entity.EventObject;
import jugistanbul.stockservice.consumer.OrderEventConsumer;
import jugistanbul.stockservice.producer.StockCheckEventProducer;
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
public class StockService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);
    private static final Consumer<Integer, EventObject> consumer = OrderEventConsumer.build();
    private static final Producer<Integer, EventObject> producer = StockCheckEventProducer.build();

    public static void main(String[] args) {

        ConsumerRecords<Integer, EventObject> records;
        try {
            EventObject event;
            while (true) {
                records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<Integer, EventObject> record : records) {
                    LOGGER.info("Consumed record in stock service method: Key {} Value {} " +
                                    "Partition {} Offset {}", record.key(), record.value(),
                            record.partition(), record.offset());

                    event = record.value();
                    event.setInStock(event.getProductId() > 0 && event.getAmount() > 0);

                    publishStockCheckEvent(event);
                }
                consumer.commitSync();
            }
        } catch (Exception ex) {
            LOGGER.error("An exception was thrown in stock service", ex);
        }
    }

    private static void publishStockCheckEvent(final EventObject event) {

        final ProducerRecord<Integer, EventObject> record =
                new ProducerRecord<>("STOCK_CHECK_EVENT_TOPIC", event.getCustomerId(), event);

        try {
            producer.beginTransaction();
            final RecordMetadata metadata = producer.send(record).get();
            producer.commitTransaction();
            LOGGER.info("Stock check event published to Kafka: Topic {} Partition {} Offset {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            LOGGER.error("An InterruptedException was thrown in publishStockCheckEvent method", e.getMessage());
        } catch (ExecutionException e) {
            LOGGER.error("An InterruptedException was thrown in publishStockCheckEvent method", e.getMessage());
        } catch (ProducerFencedException e) {
            LOGGER.error("An InterruptedException was thrown in publishStockCheckEvent method", e.getMessage());
            producer.close();
        } catch (KafkaException e) {
            LOGGER.error("A KafkaException was thrown in publishStockCheckEvent method", e.getMessage());
            producer.abortTransaction();
        }
    }
}
