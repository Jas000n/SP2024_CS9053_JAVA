package NYU.SPJAVA.utils;

//read properties from conf.properties， should be static method
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {
	public static enum CONF {
		// puts constant in enum
		// constructor allows us to get name using .name attribute

		GPT_KEY("chatgpt.api.key"),
		DB_URL("database.url"),
		DB_USER("database.user"),
		DB_PWD("database.password"),
		DB_DRIVER("database.driver"),
		APP_NAME("application.name"),
		APP_VERSION("application.version");

		public final String name;
		private CONF(String name) {  // constructor
			this.name = name;
		}
	}

	// returns value from conf.properties
	// key is an enum FROM CONF above
	public static String get(CONF key) {
        Properties properties = new Properties();
        String prop = null;


        try (InputStream input = Property.class.getClassLoader().getResourceAsStream("conf.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find conf.properties");
                return null;
            }
            properties.load(input);


            prop = properties.getProperty(key.name);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return prop;
    }

    public static void main(String[] args) {
        // System.out.println(Property.get(Property.CONF.GPT_KEY));
		System.out.println(Property.get(Property.CONF.APP_NAME));
		System.out.println(Property.get(Property.CONF.APP_VERSION));
    }
}
