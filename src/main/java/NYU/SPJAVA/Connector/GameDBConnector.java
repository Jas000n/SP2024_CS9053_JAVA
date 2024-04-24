package NYU.SPJAVA.Connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import NYU.SPJAVA.DBEntity.Game;
import NYU.SPJAVA.DBEntity.Player;
import NYU.SPJAVA.DBEntity.Word;
import NYU.SPJAVA.utils.DateTimeUtil;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;
import org.joda.time.LocalDateTime;
import NYU.SPJAVA.exceptions.*;

public class GameDBConnector extends DBConnector {
	private final static String createGameQuery = "INSERT INTO picasso.game"
			+ "(word_id, mode, status, time_limit, creator_id, capacity, created, started) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ;";

	// none of the other fields should be allowed to change
	// they should all be set before game creation
	public final static String updateGameQuery = "UPDATE picasso.game SET status = ?, started = ?, ended = ? ;";

	// gameID is the only info local does not have
	private final static String getGameQuery = "SELECT game_id, created FROM game WHERE creator_id = ? ORDER BY started DESC LIMIT 1;";

	public GameDBConnector() throws Exception {
		super();
		connect(); // connects to the db
	}

	private Integer getGameID(int creatorID, LocalDateTime created) throws Exception {
		// fetches game_id from db using creator_id and created timestamp
		PreparedStatement statement = conn.prepareStatement(GameDBConnector.getGameQuery);
		statement.setInt(1, creatorID);

		ResultSet res = statement.executeQuery();

		if (!res.next()) {
			// result set is empty
			String msg = String.format("Game does not exist!");
			throw new GameDoesNotExistException(msg);

		} else {
			int gameID = res.getInt("game_id");
			LocalDateTime createdBack = DateTimeUtil.toDateTime(res.getTimestamp("created"));
			if (created != createdBack) {
				System.out.println("unequal time stamps");
				System.out.println("Local: " + created);
				System.out.println("DB: " + createdBack);
				System.out.println("Local timestamp: " + DateTimeUtil.toTimestamp(created));
				System.out.println("DB timestamp: " + DateTimeUtil.toTimestamp(createdBack));
				System.out.println("Local Millies: " + created.toDateTime().getMillis());
				System.out.println("DB Millies: " + createdBack.toDateTime().getMillis());
				
			}
			return gameID;
		}

	}

	private PreparedStatement setNewGameFields(Game game) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(GameDBConnector.createGameQuery);
		statement.setInt(1, game.getWord().getWordID()); // getWord returns Word object
		statement.setString(2, String.valueOf(game.getMode()));
		statement.setString(3, String.valueOf(game.getStatus()));
		statement.setInt(4, game.getTimeLimit());
		statement.setInt(5, game.getCreator().getPlayerID());
		statement.setInt(6, game.getCapacity());
		statement.setTimestamp(7, DateTimeUtil.toTimestamp(game.getCreated()));
		statement.setTimestamp(8, DateTimeUtil.toTimestamp(game.getStarted()));

		return statement;
	}

	// DB doesn't care if it's single or multi game
	// it looks the same to the db table
	private Game createDBGame(Game game) throws Exception {
		PreparedStatement insertGameStatement = setNewGameFields(game);
		// insert record
		insertGameStatement.executeUpdate();
		Integer gameID = getGameID(game.getCreator().getPlayerID(), game.getCreated());

		if (gameID == null) {
			throw new GameDoesNotExistException("could not create game!");
		}
		return new Game(gameID, game.getWord(), game.getMode(), game.getStatus(), game.getTimeLimit(),
				game.getCreator(), game.getCapacity(), game.getCreated(), game.getStarted(), game.getEnded());
	}

	private Response updateDBGame(Game game) throws Exception {
		PreparedStatement statement = conn.prepareStatement(GameDBConnector.updateGameQuery);
		statement.setString(1, String.valueOf(game.getStatus()));
		statement.setTimestamp(2, DateTimeUtil.toTimestamp(game.getStarted()));
		statement.setTimestamp(3, DateTimeUtil.toTimestamp(game.getEnded()));

		try {
			statement.executeUpdate();
			return new Response(ResponseCode.SUCCESS, "Game updated!", null, null);
		} catch (Exception ex) {
			return new Response(ResponseCode.FAILED, ex.getMessage(), ex, null);

		}
	}

	// create a game, passing in a game entity, with only some of the field
	// populated
	// with word_id, mode, status, time_limit,creator_id,created, started
	// return a game object with every field populated
	public Response createGame(Game game) {
		Game newGame;
		try {
			newGame = createDBGame(game);
			String msg = String.format("Game created successfully, id %d", newGame.getGameID());
			return new Response(ResponseCode.SUCCESS, msg, null, newGame);
		} catch (Exception ex) {
			String msg = "Failed to create game";
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}

	}

	// change the data in a single game, passing in a complete game entity
	// finding the corresponding record according to primary key and update record
	// return a boolean
	public Response updateGame(Game game) {
		try {
			updateDBGame(game);
			String msg = "Game updated successfully";
			return new Response(ResponseCode.SUCCESS, msg, null, null);
		} catch (Exception ex) {
			String msg = "Failed to update game";
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}

	}
	
	public static void main(String[] args) throws Exception {
		Player p = new Player(1, "John_Wick", "21f3b88dc607cb2560f90133b90f66d4e12ff129db175c2e9c218f98937c6b74");
		Word w = new Word(12, "phone");
		Game g = new Game(w, p);
		GameDBConnector gc = new GameDBConnector();
		Response resp = gc.createGame(g);
		if (resp.code != ResponseCode.SUCCESS) {
			resp.ex.printStackTrace();
		}
	}
}
