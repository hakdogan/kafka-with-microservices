package jugistanbul.orderservice.boundary;

import jugistanbul.orderservice.client.EndPointCaller;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
@Path("order")
public class OrderRequest
{
    private final String STOCK_SERVICE_URL = "http://localhost:9082";
    private final String STOCK_SERVICE_PATH = "api/product";
    private final String VALIDATION_SERVICE_URL = "http://localhost:9084";
    private final String VALIDATION_SERVICE_PATH = "/api/validation/{cardNumber}";
    private final String PAYMENT_SERVICE_URL = "http://localhost:9086";
    private final String PAYMENT_SERVICE_PATH = "api/payment";

    @Inject
    private EndPointCaller caller;

    @Inject
    private Logger logger;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleOrderRequest(final JsonObject request) {

        final JsonObject payload = Json.createObjectBuilder()
                .add("productId", request.getInt("productId"))
                .add("amount", request.getInt("amount"))
                .build();

        if (!isProductInStock(payload)) {
            logger.info("The product is out of stock");
            return Response
                    .status(200)
                    .entity(Json.createObjectBuilder()
                            .add("response", "The product is out of stock")
                            .build())
                    .build();
        }

        if (!isCardNumberValid(request.getString("cardNumber"))) {
            logger.info("Invalid credit card number");
            return Response
                    .status(200)
                    .entity(Json.createObjectBuilder()
                            .add("response", "Invalid credit card number")
                            .build())
                    .build();
        }

        final JsonObject returnObject = caller.callGivenEndPoint(PAYMENT_SERVICE_URL,
                PAYMENT_SERVICE_PATH,
                Json.createObjectBuilder()
                        .add("cardNumber", request.getString("cardNumber"))
                        .build());
        logger.info(returnObject.toString());
        return Response.status(200)
                .entity(returnObject).build();
    }

    private boolean isProductInStock(final JsonObject payload) {

        final JsonObject response =
                caller.callGivenEndPoint(STOCK_SERVICE_URL,
                        STOCK_SERVICE_PATH, payload);

        return response.getBoolean("inStock");
    }

    private boolean isCardNumberValid(final String cardNumber) {
        final JsonObject response =
                caller.callGivenEndPoint(VALIDATION_SERVICE_URL,
                        VALIDATION_SERVICE_PATH, cardNumber);

        return response.getBoolean("approval");
    }
}
