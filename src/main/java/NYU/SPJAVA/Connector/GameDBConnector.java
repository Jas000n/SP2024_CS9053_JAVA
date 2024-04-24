package NYU.SPJAVA.Connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import NYU.SPJAVA.DBEntity.Game;
import NYU.SPJAVA.DBEntity.Player;
import NYU.SPJAVA.DBEntity.Word;
import NYU.SPJAVA.utils.DateTimeUtil;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import NYU.SPJAVA.exceptions.*;

public class GameDBConnector extends DBConnector {
	private final static String createGameQuery = "INSERT INTO picasso.game"
			+ "(word_id, mode, status, time_limit, creator_id, capacity, created, started) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ;";

	// none of the other fields should be allowed to change
	// they should all be set before game creation
	public final static String updateGameQuery = "UPDATE picasso.game SET status = ?, started = ?, ended = ? WHERE game_id = ? ;";

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
			if (DateTimeUtil.isEqualMillis(created, createdBack)) {
				return gameID;
			} else {
				throw new GameDoesNotExistException("Created Local and DB time mismatch");
			}

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
		if (game.getGameID() == null) {
			throw new SQLException("Game ID cannot be null when updating game");
		}
		
		PreparedStatement statement = conn.prepareStatement(GameDBConnector.updateGameQuery);
		statement.setString(1, String.valueOf(game.getStatus()));
		if (game.getStarted() != null) {
			statement.setTimestamp(2, DateTimeUtil.toTimestamp(game.getStarted()));
		} else {
			statement.setNull(2, Types.INTEGER);
		}
		if (game.getEnded() != null) {
			statement.setTimestamp(3, DateTimeUtil.toTimestamp(game.getEnded()));
		} else {
			statement.setNull(3, Types.INTEGER);
		}
		
		statement.setInt(4, game.getGameID());


		try {
			statement.executeUpdate();
			String msg = String.format("Game updated!, id %d", game.getGameID());
			return new Response(ResponseCode.SUCCESS, msg, null, null);
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
		} else {
			g = (Game) resp.data;  // get updated game
			System.out.println(resp);
			System.out.println(g);
		}

		// test update

		g.setStarted(new LocalDateTime());
		resp = gc.updateDBGame(g);

		if (resp.code != ResponseCode.SUCCESS) {
			resp.ex.printStackTrace();
		} else {
			System.out.println(resp);
		}
		
		g.setEnded(new LocalDateTime());
		resp = gc.updateDBGame(g);
		
		if (resp.code != ResponseCode.SUCCESS) {
			resp.ex.printStackTrace();
		} else {
			System.out.println("Ended updated successfully, " );
		}

	}
}
