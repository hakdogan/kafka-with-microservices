package jugistanbul.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/

public class CustomDeserializer<T> implements Deserializer
{
    private Class<T> type;

    public CustomDeserializer(Class type) {
        this.type = type;
    }

    public void configure(Map map, boolean b) {}

    public Object deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        Object obj = null;

        try {
            obj = mapper.readValue(bytes, this.type);
        } catch (Exception ex) {
            System.out.println("Error in deserializing object: " + ex.getMessage());
        }

        return obj;
    }

    public void close() {}
}
