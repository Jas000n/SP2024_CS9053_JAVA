package NYU.SPJAVA.Connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import NYU.SPJAVA.DBEntity.Player;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;
import NYU.SPJAVA.exceptions.*;

public class PlayerDBConnector extends DBConnector {
	private static final String getUserQuery = "SELECT player_id, uname, password FROM picasso.player WHERE uname = ? ;";

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

			if (!player.isPasswordEqual(password)) {
				String msg = "The provided password does not match";
				throw new PasswordMismatchException(msg);
			}
			// player exists and authenticated successfully!
			return new Player(playerID, uname, password);
		}
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
			// TODO: register user and return success
			String msg = String.format("User %s registered successfully, please login!", player.getUname());
			return new Response(ResponseCode.SUCCESS, msg, null, null);
		} catch (Exception ex) {
			// some other exceptions, return as is
			return new Response(ResponseCode.FAILED, ex.getMessage(), ex, null);
		}
	}

	// user login,
	// passing in a player with only uname and password(encrypted already)
	// return a complete populated player object, if failed, put error msg in
	// res_msg
	/**
	 * authenticates player and add to online set (TODO)
	 * 
	 * @param player
	 * @return
	 */
	public Response login(Player player) {
		try {
			// first check if user exists
			Player p = getUser(player);
			// TODO add to redis online set
			String msg = "User authenticated successfully!";
			return new Response(ResponseCode.SUCCESS, msg, null, p);
		} catch (Exception ex) {
			return new Response(ResponseCode.FAILED, ex.getMessage(), ex, null);
		}
	}

	// user logout,
	// passing in a complete player
	// data in res should be a boolean -- why not use status code?
	/**
	 * logs out player
	 * 
	 * @param player
	 * @return
	 */
	public Response logout(Player player) {
		// TODO: check if user in redis online set and remove
		String msg = "user logged out!";
		return new Response(ResponseCode.SUCCESS, msg, null, true);
	}

}
