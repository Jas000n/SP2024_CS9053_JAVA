package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.Game;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;

public class GameDBConnector {

	private Game createSingleGame(Game game) throws Exception {
		return null;
	}

	private Game createMultiGame(Game game) throws Exception {
		return null;
	}

	private void updateSingleGame(Game game) throws Exception {
		;
	}

	private void updateMultiGame(Game game) throws Exception {
		;
	}

	// create a single game, passing in a game entity, with only some of the field
	// populated
	// with word_id, mode, status, time_limit,creator_id,created, started
	// return a game object with every field populated
	public Response createGame(Game game) {
		Game newGame;
		try {
			if (game.getMode() == 'S') {
				newGame = createSingleGame(game);
			} else {
				// TODO: implement multi game
				newGame = createMultiGame(game);
			}
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
			if (game.getMode() == 'S') {
				updateSingleGame(game);
			} else {
				//TODO: implement MultiGame
				updateMultiGame(game);
			}
			String msg = "Game updated successfully";
			return new Response(ResponseCode.SUCCESS, msg, null, null);
		} catch (Exception ex) {
			String msg = "Failed to update game";
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}

	}
}
