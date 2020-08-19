package jugistanbul.orderservice.boundary;

import jugistanbul.entity.EventObject;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
@Path("order")
public class OrderRequest {

    @Inject
    private Logger logger;

    @Inject
    private Producer<Integer, EventObject> producer;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleOrderRequest(final EventObject orderEvent) {
        publishToKafka(orderEvent);
    }

    private void publishToKafka(final EventObject orderEvent) {

        final ProducerRecord<Integer, EventObject> record =
                new ProducerRecord<>("ORDER_EVENT_TOPIC",
                        orderEvent.getCustomerId(),
                        orderEvent);

        try {
            producer.beginTransaction();
            final RecordMetadata metadata = producer.send(record).get();
            producer.commitTransaction();
            logger.info("Agreement signed event published to Kafka: " +
                    "Topic {} Partition {}  Offset {}", metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            logger.error("An InterruptedException was thrown", e.getMessage());
        } catch (ExecutionException e) {
            logger.error("An ExecutionException was thrown", e.getMessage());
        } catch (ProducerFencedException e) {
            logger.error("A ProducerFencedException was thrown", e.getMessage());
            producer.close();
        } catch (KafkaException e) {
            logger.error("A KafkaException was thrown", e.getMessage());
            producer.abortTransaction();
        }
    }
}
