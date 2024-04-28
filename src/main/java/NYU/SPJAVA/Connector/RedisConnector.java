package NYU.SPJAVA.Connector;

import redis.clients.jedis.JedisPooled;
import NYU.SPJAVA.utils.Property;
import NYU.SPJAVA.utils.Property.CONF;

public class RedisConnector {

	public static void main(String[] args) {
		String url = Property.get(CONF.REDIS_URL);
		int port = Integer.parseInt(Property.get(CONF.REDIS_PORT));
		JedisPooled jedis = new JedisPooled(url, port);
		jedis.set("test", "it's working!");
		System.out.println(jedis.get("test"));
		jedis.del("test");
		}
	}
