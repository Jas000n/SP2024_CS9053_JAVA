package NYU.SPJAVA.DBEntity;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

public class Player {
	private final Integer playerID; // can be null before register/login
	private final String uname;
	private final String password;
	
	public Player(String uname, String password) {
		// allow constructing player with just uname and password
		this(null, uname, password);
	}

	public Player(Integer PlayerID, String uname, String password) {
		this.playerID = PlayerID;
		this.uname = uname;
		this.password = Player.hashPassword(password);
	}

	public static String hashPassword(String password) {
		return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
	}
	
	public Integer getPlayerID() {
		// needs to return Integer not int
		// because primitive types cannot be null
		return this.playerID;
	}
	
	public String getUname() {
		return this.uname;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	@Override
	public String toString() {
		return String.format("Player: %s, id: %s", this.uname, this.playerID);
	}

	public static void main(String[] args) {
		// test player
    	String pwd = "hello world!";
    	String hash = Hashing.sha256().hashString(pwd, StandardCharsets.UTF_8).toString();
    	
    	Player p = new Player(null, "username", pwd);
    	System.out.print(p.getPassword().equals(hash));
    }

}
