package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.Player;
import NYU.SPJAVA.utils.Response;

public class PlayerDBConnector {

    //register a user,
    // passing a player object, with only uname and password(encrypted already, DB connector shouldnt worry about encrypt)
    // return a complete populated player object
    public Response Register(Player player){
        return null;
    }
    // user login,
    // passing in a player with only uname and password(encrypted already)
    // return a complete populated player object, if failed, put error msg in res_msg
    public Response Login(Player player){
        return null;
    }
    // user logout,
    // passing in a complete player
    // data in res should be a boolean
    public Response Logout(Player player){
        return null;
    }

}
