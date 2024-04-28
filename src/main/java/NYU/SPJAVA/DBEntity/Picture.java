package NYU.SPJAVA.DBEntity;

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

public class Picture {
	private Integer pictureID; // has to be Integer to be nullable
	private Game game;
	private Player player;
	private Integer score;
	private String remark;
	private String title;

	public Picture(Integer pictureID, Game game, Player player, Integer score, String remark, String title) {
		this.pictureID = pictureID;
		this.game = game;
		this.player = player;
		this.score = score;
		this.remark = remark;
		this.title = title;
	}

	public Picture(Game game, Player player) {
		this(null, game, player, null, null, null);
	}

	public Integer getPictureID() {
		return pictureID;
	}

	public void setPictureID(int pictureID) {
		this.pictureID = pictureID;
	}

	public Game getGame() {
		return this.game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

//    public Integer getGame_id() {
//        return game.getGameID();
//    }

//    public void setGame_id(int game_id) {
//        this.game_id = game_id;
//    }

//    public int getPlayer_id() {
//        return player_id;
//    }
//
//    public void setPlayer_id(int player_id) {
//        this.player_id = player_id;
//    }

	public Integer getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Picture | id: " + this.pictureID + " title: " + this.title + " Author: " + this.player.getUname()
				+ " Score: " + this.score + " Remark: " + this.remark;
	}
}
