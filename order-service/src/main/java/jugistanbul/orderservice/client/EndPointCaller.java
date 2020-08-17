package jugistanbul.orderservice.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 17.08.2020
 **/
@ApplicationScoped
public class EndPointCaller
{
    private WebTarget target;
    private Client client;

    @PostConstruct
    public void initClient() {
        final ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        client = clientBuilder.build();
    }

    public JsonObject callGivenEndPoint(final String uri, final String path,
                                        final JsonObject payload){
        target = client.target(uri)
                .path(path);

        final Response response = target
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON));

        return response.readEntity(JsonObject.class);
    }

    public JsonObject callGivenEndPoint(final String uri, final String path,
                                        final String value){
        target = client.target(uri)
                .path(path)
                .resolveTemplate("cardNumber", value);

        final Response response = target
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        return response.readEntity(JsonObject.class);
    }

    @PreDestroy
    public void tearDown() {
        client.close();
    }
}
