package NYU.SPJAVA.Connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.joda.time.LocalDateTime;

import NYU.SPJAVA.DBEntity.*;
import NYU.SPJAVA.exceptions.*;
import NYU.SPJAVA.utils.DateTimeUtil;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;

public class PictureDBConnector extends DBConnector {
	private final static String getMyPictureQuery = "SELECT picture_id, score, remark, title FROM picture WHERE game_id = ? and player_id = ?;";
	// private final static String getGamePicturesQuery = "SELECT picture_id,
	// player_id, score, remark, title WHERE game_id = ? ;";
	private final static String insertPictureQuery = "INSERT INTO picture(game_id, player_id, score, remark, title) "
			+ "VALUES (?, ?, ?, ?, ?);";
	private final static String updatePictureQuery = "UPDATE picasso.picture SET score = ?, remark = ?, title = ? "
			+ "WHERE picture_id = ? ;";

	public PictureDBConnector() throws Exception {
		super();
		connect(); // connects to the db
	}

	Picture getMyPicture(Game game, Player player) throws Exception {
		// fetches game_id from db using creator_id and created timestamp
		PreparedStatement statement = conn.prepareStatement(PictureDBConnector.getMyPictureQuery);
		statement.setInt(1, game.getGameID());
		statement.setInt(2, player.getPlayerID());

		ResultSet res = statement.executeQuery();

		if (!res.next()) {
			// result set is empty
			String msg = String.format("Picture does not exist!");
			throw new PictureDoesNotExistException(msg);

		} else {
			int pictureID = res.getInt("picture_id");
			int score = res.getInt("score");
			String remark = res.getString("remark");
			String title = res.getString("title");
			return new Picture(pictureID, game, player, score, remark, title);
		}

	}

	private Picture insertDBPicture(Picture picture) throws Exception {
		PreparedStatement statement = conn.prepareStatement(PictureDBConnector.insertPictureQuery);
		// game_id, player_id, score, remark, title
		statement.setInt(1, picture.getGame().getGameID());
		statement.setInt(2, picture.getPlayer().getPlayerID());
		if (picture.getScore() != null) {
			statement.setInt(3, picture.getScore());
		} else {
			statement.setNull(3, Types.INTEGER);
		}

		if (picture.getRemark() != null) {
			statement.setString(4, picture.getRemark());
		} else {
			statement.setNull(4, Types.VARCHAR);
		}

		if (picture.getTitle() != null) {
			statement.setString(5, picture.getTitle());
		} else {
			statement.setNull(5, Types.VARCHAR);
		}

		statement.executeUpdate();

		// fetch new
		Picture newPicture = getMyPicture(picture.getGame(), picture.getPlayer());
		if (newPicture.getPictureID() == null) {
			throw new PictureDoesNotExistException("could not create picture!");
		} else {
			return newPicture;
		}
	}

	private void updateDBPicture(Picture picture) throws Exception {
		if (picture.getPictureID() == null) {
			throw new IllegalArgumentException("picture_id cannot be null");
		}

		PreparedStatement statement = conn.prepareStatement(PictureDBConnector.updatePictureQuery);
		if (picture.getScore() != null) {
			statement.setInt(1, picture.getScore());
		} else {
			statement.setNull(1, Types.INTEGER);
		}

		if (picture.getRemark() != null) {
			statement.setString(2, picture.getRemark());
		} else {
			statement.setNull(2, Types.VARCHAR);
		}

		if (picture.getTitle() != null) {
			statement.setString(3, picture.getTitle());
		} else {
			statement.setNull(3, Types.VARCHAR);
		}

		statement.setInt(4, picture.getPictureID());

		statement.executeUpdate();
	}

	public Response createPicture(Picture picture) {
		try {
			Picture newPicture = insertDBPicture(picture);
			String msg = String.format("picture added successfully, id %d", newPicture.getPictureID());
			return new Response(ResponseCode.SUCCESS, msg, null, newPicture);
		} catch (Exception ex) {
			String msg = "could not add picture!";
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}
	}

	public Response updatePicture(Picture picture) {
		try {
			updateDBPicture(picture);
			String msg = String.format("picture updated successfully, id %d", picture.getPictureID());
			return new Response(ResponseCode.SUCCESS, msg, null, null);
		} catch (Exception ex) {
			String msg = "could not update picture!";
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}
	}

	public static void main(String[] args) throws Exception {
		// example of how to create a picutre
		// skipping error checking code for clarity
		Response resp;

		WordDBConnector wc = new WordDBConnector();
		GameDBConnector gc = new GameDBConnector();
		PictureDBConnector pc = new PictureDBConnector();

		// get random word. Assumes words exist
		resp = wc.getWord(1);
		System.out.println(resp);
		Word word = (Word) ((ArrayList<Word>) resp.data).get(0);

		// assume these two players exist
		Player player1 = new Player(1, "John_Wick", "I am back!");
		Player player2 = new Player(2, "The_Elder", "The high table");

		// create a game, player1 here is game creator
		Game game = new Game(word, player1);

		// add game to DB and get back a new Game with gameID populated
		resp = gc.createGame(game);
		System.out.println(resp);
		game = (Game) resp.data;

		// multi-player game looks the same as single game to the DB
		// player 1 created the game and along with player2 joined the game
		// note that gameID has to exist at this point or this fails to create picture
		Picture picture1 = new Picture(game, player1);
		Picture picture2 = new Picture(game, player2);

		// add picture to db and get back populated Picture
		resp = pc.createPicture(picture1);
		System.out.println(resp);
		picture1 = (Picture) resp.data;

		resp = pc.createPicture(picture2);
		System.out.println(resp);
		picture2 = (Picture) resp.data;

		// start game
		game.setStarted(new LocalDateTime());
		resp = gc.updateGame(game);
		System.out.println(resp);

		// play code here
		

		// game ended, update end time
		game.setEnded(new LocalDateTime());
		resp = gc.updateGame(game);
		System.out.println(resp);

		// scoring code here
		picture1.setScore(100);
		picture1.setRemark("a perfect phone!");
		picture1.setTitle("The John Wick Phone");

		picture2.setScore(5);
		picture2.setRemark("That is not a phone!");
		picture2.setTitle("The Elder Phone");

		// update score, remark and title
		resp = pc.updatePicture(picture1);
		System.out.println(resp);
		resp = pc.updatePicture(picture2);
		System.out.println(resp);

		// fetch all players for a specific game
		resp = gc.getGamePlayers(game);
		System.out.println(resp);
		ArrayList<Player> players = (ArrayList<Player>) resp.data;

		System.out.println("players for this game are: ");
		for (Player p : players) {
			System.out.println(p.toString());
		}

		// fetch all pictures for a specific game
		resp = gc.getGamePictures(game);
		System.out.println(resp);
		ArrayList<Picture> pictures = (ArrayList<Picture>) resp.data;

		System.out.println("Pictures for this game are: ");
		for (Picture p : pictures) {
			System.out.println(p.toString());
		}

	}

}
