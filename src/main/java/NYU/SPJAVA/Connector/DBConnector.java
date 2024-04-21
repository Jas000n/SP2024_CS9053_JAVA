package NYU.SPJAVA.Connector;

import NYU.SPJAVA.utils.Property;
import NYU.SPJAVA.utils.Property.CONF;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
	protected Connection conn = null;
	
	public void connect() throws Exception {
		// load driver
		
		String url = Property.get(CONF.DB_URL);
		String user = Property.get(CONF.DB_USER);
		String pwd = Property.get(CONF.DB_PWD);
		
		try {
			// load MySQL Driver (generally unnecessary)
			Class.forName(Property.get(CONF.DB_DRIVER));
			
			// connect
			System.out.println("testing connection to database");
			conn = DriverManager.getConnection(url, user, pwd);
			System.out.println("connection successful!");
			
		} catch (Exception e) {
			System.out.println("connection failed!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		DBConnector db = new DBConnector();
		db.connect();
	}
}
