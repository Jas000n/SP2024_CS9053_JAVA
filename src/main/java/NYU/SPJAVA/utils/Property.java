package NYU.SPJAVA.utils;

//read properties from conf.properties， should be static method
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {


    // method to get ChatGPT Api Key from resources/conf.properties
    public static String getChatGPTApiKey() {
        Properties properties = new Properties();
        String apiKey = null;


        try (InputStream input = Property.class.getClassLoader().getResourceAsStream("conf.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find conf.properties");
                return null;
            }
            properties.load(input);


            apiKey = properties.getProperty("chatgpt.api.key");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return apiKey;
    }

    public static void main(String[] args) {
        System.out.println(Property.getChatGPTApiKey());
    }
}
