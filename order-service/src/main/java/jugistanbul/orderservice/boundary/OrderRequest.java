package jugistanbul.orderservice.boundary;

import jugistanbul.entity.EventObject;
import jugistanbul.orderservice.kafka.publish.KafkaPublisher;
import org.apache.kafka.clients.producer.Producer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
@Path("order")
public class OrderRequest {

    @Inject
    private KafkaPublisher kafkaPublisher;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleOrderRequest(final EventObject event) {
        event.setEvent("order");
        kafkaPublisher.publishRecordToKafka("ORDER_EVENT_TOPIC", event);
    }
}
