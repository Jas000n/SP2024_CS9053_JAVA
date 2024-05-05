package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.DoubleGameRoom;
import NYU.SPJAVA.DBEntity.PlayerVO;
import redis.clients.jedis.JedisPooled;
import NYU.SPJAVA.utils.Property;
import NYU.SPJAVA.utils.Property.CONF;
import java.util.ArrayList;
import java.util.Set;

public class RedisConnector {
	private JedisPooled jedis;

	public RedisConnector() {
		String url = Property.get(CONF.REDIS_URL);
		int port = Integer.parseInt(Property.get(CONF.REDIS_PORT));
		this.jedis = new JedisPooled(url, port);
	}

	public ArrayList<PlayerVO> getAllOnlinePlayers() {
		ArrayList<PlayerVO> onlinePlayers = new ArrayList<>();
		Set<String> keys = jedis.keys("player:*");
		for (String key : keys) {
			String status = jedis.hget(key, "status");
			String playerID = jedis.hget(key, "playerID");
			String uname = jedis.hget(key, "uname");
			playerID = key.split(":")[1];
			PlayerVO player = new PlayerVO(playerID, uname, status);
			onlinePlayers.add(player);
			}

		return onlinePlayers;
	}

	public boolean updatePlayerStatus(PlayerVO player) {
		String key = "player:" + player.getPlayerID();
		jedis.hset(key, "status", player.getStatus());
		jedis.hset(key, "uname", player.getUname());
		return true;
	}

	public boolean playerOffline(PlayerVO player) {
		String key = "player:" + player.getPlayerID();
		jedis.del(key);
		return true;
	}

	public boolean hostInvitePlayer(int hostID, int playerID, String word) {
		String hostKey = "player:" + hostID;
		String playerKey = "player:" + playerID;

		// Retrieve the status of both players from Redis
		String hostStatus = jedis.hget(hostKey, "status");
		String playerStatus = jedis.hget(playerKey, "status");
		if (hostStatus.equals("Online") && playerStatus.equals("Online")) {
			DoubleGameRoom gameRoom = new DoubleGameRoom(hostID, playerID, word, 0, 0,"init");
			String key = "gameRoom:" + hostID;
			jedis.hset(key, "hostPlayerID", String.valueOf(gameRoom.getHostPlayerID()));
			jedis.hset(key, "invitedPlayerID", String.valueOf(gameRoom.getInvitedPlayerID()));
			jedis.hset(key, "word", gameRoom.getWord());
			jedis.hset(key, "hostScore", String.valueOf(gameRoom.getHostScore()));
			jedis.hset(key, "invitedScore", String.valueOf(gameRoom.getInvitedScore()));
			jedis.hset(key, "status", gameRoom.getStatus());
			jedis.hset(key, "numOfCheck", String.valueOf(gameRoom.getNumOfCheck()));
			return true;
		}else {
			return false;
		}
	}

	public DoubleGameRoom retrieveRoomByInvitedID(int invitedPlayerID) {
		Set<String> keys = jedis.keys("gameRoom:*");
		for (String key : keys) {
			if (String.valueOf(invitedPlayerID).equals(jedis.hget(key, "invitedPlayerID"))) {
				return new DoubleGameRoom(
						Integer.parseInt(jedis.hget(key, "hostPlayerID")),
						invitedPlayerID,
						jedis.hget(key, "word"),
						Integer.parseInt(jedis.hget(key, "hostScore")),
						Integer.parseInt(jedis.hget(key, "invitedScore")),
						jedis.hget(key, "status"),
						Integer.parseInt(jedis.hget(key, "numOfCheck"))
				);
			}
		}
		return null;
	}

	public boolean updateGameRoom(DoubleGameRoom gameRoom) {
		String key = "gameRoom:" + gameRoom.getHostPlayerID();
		jedis.hset(key, "hostScore", String.valueOf(gameRoom.getHostScore()));
		jedis.hset(key, "invitedScore", String.valueOf(gameRoom.getInvitedScore()));
		jedis.hset(key, "status", gameRoom.getStatus());
		jedis.hset(key,"numOfCheck",String.valueOf(gameRoom.getNumOfCheck()));
		return true;
	}

	public boolean removeGameRoom(DoubleGameRoom gameRoom) {
		String key = "gameRoom:" + gameRoom.getHostPlayerID();
		jedis.del(key);
		return true;
	}

	public DoubleGameRoom retrieveRoomByHostID(int hostPlayerID) {
		String key = "gameRoom:" + hostPlayerID;
		if (jedis.exists(key)) {
			return new DoubleGameRoom(
					hostPlayerID,
					Integer.parseInt(jedis.hget(key, "invitedPlayerID")),
					jedis.hget(key, "word"),
					Integer.parseInt(jedis.hget(key, "hostScore")),
					Integer.parseInt(jedis.hget(key, "invitedScore")),
					jedis.hget(key, "status"),
					Integer.parseInt(jedis.hget(key,"numOfCheck"))
			);
		}
		return null;
	}
}
