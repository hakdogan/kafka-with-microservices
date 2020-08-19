package jugistanbul.orderservice.kafka.event.stockcheck.sink;

import jugistanbul.entity.EventObject;
import org.slf4j.Logger;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/
@Stateless
public class StockCheckEventSink
{
    @Inject
    private Logger logger;

    public void onMessage(@Observes EventObject event) {
        if(!event.isInStock()) {
            logger.info("The product {} is out of stock", event.getProductId());
        }
    }
}
