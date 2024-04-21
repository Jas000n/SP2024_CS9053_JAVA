package NYU.SPJAVA.Connector;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.hash.Hashing;

import NYU.SPJAVA.DBEntity.Player;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;
import NYU.SPJAVA.exceptions.*;

public class PlayerDBConnector extends DBConnector {
	private static final String getUserQuery = "SELECT player_id, uname, password FROM picasso.player WHERE uname = ? ;";
	private static final String addUserQuery = "INSERT INTO picasso.player(uname, password) VALUES (?, ?) ;";
	
	public PlayerDBConnector() throws Exception {
		super();
		connect(); // connects to the db
	}

	/**
	 * Fetch user from db
	 * 
	 * @param player without playerID
	 * @return new player with playerID populated
	 * @throws UserDoesNotExistException
	 * @throws PasswordMismatchException
	 * @throws SQLException
	 */
	private Player getUser(Player player) throws UserDoesNotExistException, PasswordMismatchException, SQLException {
		PreparedStatement statement = conn.prepareStatement(PlayerDBConnector.getUserQuery);
		statement.setString(1, player.getUname());
		ResultSet res = statement.executeQuery();

		if (!res.next()) {
			// result set is empty
			String msg = String.format("User %s does not exist!", player.getUname());
			throw new UserDoesNotExistException(msg);

		} else {
			int playerID = res.getInt("player_id");
			String uname = res.getString("uname");
			String password = res.getString("password");

			if (!player.getPassword().equals(password)) {
				String msg = "The provided password does not match";
				throw new PasswordMismatchException(msg);
			}
			// player exists and authenticated successfully!
			return new Player(playerID, uname, password);
		}
	}
	
	private Player addUser(Player player) throws SQLException, UserDoesNotExistException, PasswordMismatchException {
		PreparedStatement statement = conn.prepareStatement(PlayerDBConnector.addUserQuery);
		statement.setString(1, player.getUname());
		statement.setString(2, player.getPassword());
		
		// add user to db
		statement.executeUpdate();
		
		// confirm the user was added
		// populate playerID
		return getUser(player);
	}

	/**
	 * register a player if they don't exist
	 * 
	 * @param player
	 * @return Response response Exceptions: UserExistsException, SQLException,
	 *         Exception
	 */
	public Response register(Player player) {
		try {
			// first check if user exists
			Player p = getUser(player);
			throw new UserExistsException(null); // handle in the catch
		} catch (PasswordMismatchException | UserExistsException ex) {
			// user exists
			String msg = String.format("User %s already exists!", player.getUname());
			return new Response(ResponseCode.FAILED, msg, new UserExistsException(msg), null);
		} catch (UserDoesNotExistException ex) {
			try {
				// register player and return success
				// data field contains new player object
				Player newPlayer = addUser(player);
				String msg = String.format("User %s registered successfully, please login!", newPlayer.getUname());
				return new Response(ResponseCode.SUCCESS, msg, null, newPlayer);
			} catch (Exception e) {
				String msg = String.format("Failed to create new user %s!", player.getUname());
				return new Response(ResponseCode.FAILED, msg, e, null);
			}
			
		} catch (Exception ex) {
			// some other exceptions, return as is
			return new Response(ResponseCode.FAILED, ex.getMessage(), ex, null);
		}
	}

	/**
	 * authenticates player
	 * 
	 * @param player
	 * @return new player instance with playerID populated
	 * TODO: add player to Redis
	 */
	public Response login(Player player) {
		try {
			// first check if user exists
			Player p = getUser(player);
			// TODO add user to Redis online set
			String msg = "User authenticated successfully!";
			return new Response(ResponseCode.SUCCESS, msg, null, p);
		} catch (Exception ex) {
			return new Response(ResponseCode.FAILED, ex.getMessage(), ex, null);
		}
	}

	/**
	 * logs out player
	 * 
	 * @param player
	 * @return
	 * TODO: remove player from Redis
	 */
	public Response logout(Player player) {
		// TODO: remove user from Redis online set
		String msg = "user logged out!";
		return new Response(ResponseCode.SUCCESS, msg, null, null);
	}
	
	public static void main(String args[]) throws Exception {
		// test creating and adding players
		String pwd1 = Hashing.sha256().hashString("I am back!", StandardCharsets.UTF_8).toString();
		String pwd2 = Hashing.sha256().hashString("The high table", StandardCharsets.UTF_8).toString();
		Player p1 = new Player("John_Wick", pwd1);
		Player p2 = new Player("The_Elder", pwd2);
		
		System.out.println(p1.toString());
		
		PlayerDBConnector pc = new PlayerDBConnector();
		
		Response resp = null;
		try {
			resp = pc.login(p1);
			throw resp.ex;
		} catch (UserDoesNotExistException e) {
			System.out.println(resp.toString());
		} catch (Exception o) {
			o.printStackTrace();
		}
		
		
		// register player
		Response resp1 = pc.register(p1);
		Response resp2 = pc.register(p2);
		
		Player newP1, newP2;
		
		if (resp1.code == ResponseCode.SUCCESS && resp1.code == ResponseCode.SUCCESS) {
			// new players registered successfully!
			newP1= (Player)resp1.data;
			newP2 = (Player)resp2.data;
			System.out.println(resp1.toString());
			System.out.println(newP1.toString());
			System.out.println(resp2.toString());
			System.out.println(newP2.toString());
			
			// log in players
			resp1 = pc.login(p1);
			resp2 = pc.login(p2);
			System.out.println(resp1.toString());
			System.out.println(resp2.toString());
			System.out.println(resp1.data.toString());
			System.out.println(resp2.data.toString());
			
		} else {
			// something went wrong
			
			System.out.println(resp1.toString());
			resp1.ex.printStackTrace();
			System.out.println(resp2.toString());
			resp2.ex.printStackTrace();
		}

	}

}
