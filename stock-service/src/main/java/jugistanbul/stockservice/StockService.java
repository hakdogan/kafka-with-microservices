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
import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
public class StockService
{
    private final static Consumer<Integer, EventObject> consumer = OrderEventConsumer.build();
    private final static Producer<Integer, EventObject> producer = StockCheckEventProducer.build();

    public static void main(String[] args){

        ConsumerRecords<Integer, EventObject> records;
        try {
            EventObject event;
            while (true) {
                records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<Integer, EventObject> record : records) {
                    System.out.println("Consumed record in stock service method: Key " + record.key()
                            + " Value {} " + record.value() + " Partition "
                            + record.partition() + " Offset " + record.offset());

                    event = record.value();
                    event.setInStock(event.getProductId() > 0 && event.getAmount() > 0);

                    publishStockCheckEvent(event);
                }
                consumer.commitSync();
            }
        } catch (Exception ex) {
            System.out.println("An exception was thrown in stock service");
        }
    }

    private static void publishStockCheckEvent(final EventObject event) {

        final ProducerRecord<Integer, EventObject> record =
                new ProducerRecord<>("STOCK_CHECK_EVENT_TOPIC",
                        event.getCustomerId(), event);

        try {
            producer.beginTransaction();
            final RecordMetadata metadata = producer.send(record).get();
            producer.commitTransaction();
            System.out.println("Stock check event published to Kafka: Topic "
                    + metadata.topic() + " Partition "
                    + metadata.partition() + " Offset "
                    + metadata.offset());
        } catch (InterruptedException e) {
            System.out.println("An InterruptedException was thrown in publishStockCheckEvent method " + e.getMessage());
        } catch (ExecutionException e) {
            System.out.println("An InterruptedException was thrown in publishStockCheckEvent method " + e.getMessage());
        } catch (ProducerFencedException e) {
            System.out.println("An InterruptedException was thrown in publishStockCheckEvent method " + e.getMessage());
            producer.close();
        } catch (KafkaException e) {
            System.out.println("A KafkaException was thrown in publishStockCheckEvent method " + e.getMessage());
            producer.abortTransaction();
        }
    }
}
