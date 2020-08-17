package jugistanbul.paymentservice.baundary;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
@Path("payment")
public class ChargeRequest
{
    @POST
    public Response charge(final JsonObject object){

        return Response.status(200)
                .entity(Json.createObjectBuilder()
                .add("result", "The amount was charged from credit card "
                        + object.getString("cardNumber"))
                        .build())
                .build();
    }
}
