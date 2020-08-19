package jugistanbul.orderservice.kafka.event.stockcheck.consumer;

import jugistanbul.deserializer.CustomDeserializer;
import jugistanbul.entity.EventObject;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import static java.util.Arrays.asList;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/

public class EventConsumer implements Runnable
{
    private final KafkaConsumer<Integer, EventObject> consumer;
    private final Consumer<EventObject> eventConsumer;
    private final AtomicBoolean closed = new AtomicBoolean();

    public EventConsumer(final Properties props, final Consumer<EventObject> eventConsumer, final String... topics) {
        this.eventConsumer = eventConsumer;
        this.consumer = new KafkaConsumer<>(props, new IntegerDeserializer(),
                new CustomDeserializer<>(EventObject.class));
        this.consumer.subscribe(asList(topics));
    }

    @Override
    public void run() {
        try {
            while (!closed.get()) {
                consume();
            }
        } catch (WakeupException e) {
            if(!closed.get()) throw e;
        } finally {
            consumer.close();
        }
    }

    private void consume(){
        consumer.poll(Duration.ofMillis(100))
                .forEach(record -> eventConsumer.accept(record.value()));
        this.consumer.commitSync();
    }

    public void stop() {
        closed.set(true);
        consumer.wakeup();
    }
}
