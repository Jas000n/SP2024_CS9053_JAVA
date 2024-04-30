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
			if ("Online".equals(status)) {
				PlayerVO player = new PlayerVO(jedis.hget(key, "playerID"), jedis.hget(key, "uname"), status);
				onlinePlayers.add(player);
			}
		}
		return onlinePlayers;
	}

	public boolean updatePlayerStatus(PlayerVO player) {
		System.out.println(123);
		String key = "player:" + player.getPlayerID();
		jedis.hset(key, "status", player.getStatus());
		return true;
	}

	public boolean playerOffline(PlayerVO player) {
		String key = "player:" + player.getPlayerID();
		jedis.del(key);
		return true;
	}

	public boolean hostInvitePlayer(int hostID, int playerID, String word) {
		DoubleGameRoom gameRoom = new DoubleGameRoom(hostID, playerID, word, 0, 0);
		String key = "gameRoom:" + hostID;
		jedis.hset(key, "hostPlayerID", String.valueOf(gameRoom.getHostPlayerID()));
		jedis.hset(key, "invitedPlayerID", String.valueOf(gameRoom.getInvitedPlayerID()));
		jedis.hset(key, "word", gameRoom.getWord());
		jedis.hset(key, "hostScore", String.valueOf(gameRoom.getHostScore()));
		jedis.hset(key, "invitedScore", String.valueOf(gameRoom.getInvitedScore()));
		return true;
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
						Integer.parseInt(jedis.hget(key, "invitedScore"))
				);
			}
		}
		return null;
	}

	public boolean updateGameRoom(DoubleGameRoom gameRoom) {
		String key = "gameRoom:" + gameRoom.getHostPlayerID();
		jedis.hset(key, "hostScore", String.valueOf(gameRoom.getHostScore()));
		jedis.hset(key, "invitedScore", String.valueOf(gameRoom.getInvitedScore()));
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
					Integer.parseInt(jedis.hget(key, "invitedScore"))
			);
		}
		return null;
	}
}
