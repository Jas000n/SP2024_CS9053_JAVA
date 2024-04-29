package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.DoubleGameRoom;
import NYU.SPJAVA.DBEntity.PlayerVO;
import redis.clients.jedis.JedisPooled;
import NYU.SPJAVA.utils.Property;
import NYU.SPJAVA.utils.Property.CONF;

import java.util.ArrayList;

public class RedisConnector {

	// serialized value object in redis
//	public 	class PlayerVO {
//		private String playerID;
//		private String uname;
//		private String status;// "Online","Reviewing","In-Game","Invited"
//	}
//	public class DoubleGameRoom {
//		private int hostPlayerID; //player ID of host player
//		private int invitedPlayerID; //player ID of invited player
//		private String word; //word for this game
//		private int hostScore; // score host got
//		private int invitedScore; //score the other player got
//
//	}

	public static void main(String[] args) {
		String url = Property.get(CONF.REDIS_URL);
		int port = Integer.parseInt(Property.get(CONF.REDIS_PORT));
		JedisPooled jedis = new JedisPooled(url, port);
		jedis.set("test", "it's working!");
		System.out.println(jedis.get("test"));
		jedis.del("test");
	}
	//return all online player along with their status
	public ArrayList<PlayerVO> getAllOnlinePlayer(){
		ArrayList<PlayerVO> dummyResult = new ArrayList<PlayerVO>();
		dummyResult.add(new PlayerVO("1","admin","Online"));
		dummyResult.add(new PlayerVO("2","John-Wick","In-Game"));
		dummyResult.add(new PlayerVO("3","Tony-Stark","Reviewing"));
		return dummyResult;
	}


	// can be (add a player online, add him in redis),(a player is reviewing, set his status to reviewing) ...
	// he is in game, set his status to in game
	public boolean updatePlayerStatus(PlayerVO player){
		return false;
	}
	// player log out, remove him from redis
	public boolean playerOffline(PlayerVO player){
		return false;
	}
	// host invite a player, create a Double Game Room and put it in redis
	public boolean hostInvitePlayer(int hostID, int playerID, String word){
		return false;
	}
	// check player's invited in which game
	public DoubleGameRoom retrieveRoomByInvitedID(int invitedPlayerID){
		return null;
	}
	//update double game room(by host id)
	public boolean updateGameRoom(DoubleGameRoom gameRoom){
		return false;
	}
	//remove double game( by host id)
	public boolean removeGameRoom(DoubleGameRoom gameRoom){
		return false;
	}
	//retrieve double game room(by host id)
	public DoubleGameRoom retrieveRoomByHostID(int invitedPlayerID){
		return null;
	}


}

