package jugistanbul.stockservice.boundary;

import org.slf4j.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
@Path("product")
public class ProductRequest
{
    @Inject
    private Logger logger;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkProduct(final JsonObject request) {

        boolean inStock = false;

        if (request.getInt("productId") > 0
                && request.getInt("amount") > 0) {
            inStock = true;
        } else {
            logger.info("The product {} is out of stock", request.getInt("productId"));
        }

        return Response
                .status(200)
                .entity(Json.createObjectBuilder().add("inStock", inStock).build())
                .build();
    }
}
