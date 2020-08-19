package jugistanbul.orderservice.kafka.sink;

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
public class EventSink {
    @Inject
    private Logger logger;

    public void onMessage(@Observes EventObject event) {
        printMessage(event);
    }

    private void printMessage(final EventObject event) {

        switch (event.getEvent()) {

            case "stock-check":
                if (!event.isInStock()) {
                    logger.info("Stock check event consumed. The product {} is out of stock", event.getProductId());
                } else {
                    logger.info("Stock check event consumed. The product {} is in stock", event.getProductId());
                }
                break;
            case "validation":
                if (!event.isNumberValid()) {
                    logger.info("Validation event consumed. The card number {} is invalid", event.getCardNumber());
                } else {
                    logger.info("Validation event consumed. The card number {} is valid", event.getCardNumber());
                }
                break;
            case "billing":
                logger.info("Billing event consumed. The total price is {}", event.getPrice());
                break;

            default:
        }
    }
}
