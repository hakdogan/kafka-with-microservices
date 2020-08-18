package jugistanbul.orderservice.kafka.event.consumer;

import jugistanbul.entity.EventObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Properties;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/
@Startup
@Singleton
public class StockCheckEventConsumer
{
    private EventConsumer eventConsumer;

    @Resource
    private ManagedExecutorService service;

    @Inject
    private Event<EventObject> events;

    @PostConstruct
    public void initConsumer(){

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stockCheckEventConsumerGroup01");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        eventConsumer = new EventConsumer(props, ev -> events.fire(ev),
                "STOCK_CHECK_EVENT_TOPIC");
        service.execute(eventConsumer);
    }

    @PreDestroy
    public void shutdown() {
        eventConsumer.stop();
    }
}
