package jugistanbul.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/

public class CustomSerializer
{
    public CustomSerializer() {}

    public void configure(Map map, boolean b) {}

    public byte[] serialize(String s, Object o) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (Exception ex) {
            System.out.println("Error in serializing object: " + ex.getMessage());
        }

        return retVal;
    }

    public void close() {}
}
