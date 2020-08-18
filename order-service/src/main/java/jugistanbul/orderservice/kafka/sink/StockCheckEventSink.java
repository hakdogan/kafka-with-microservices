package jugistanbul.orderservice.kafka.sink;

import jugistanbul.entity.EventObject;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/
@Stateless
public class StockCheckEventSink
{
    public void onMessage(@Observes EventObject event) {
        System.out.println("The product {" + event.getProductId() + "} is out of stock");
    }
}
