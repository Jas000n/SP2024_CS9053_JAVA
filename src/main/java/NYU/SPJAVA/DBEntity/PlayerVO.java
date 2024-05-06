package NYU.SPJAVA.DBEntity;

public 	class PlayerVO{
		private String playerID;
		private String uname;
		private String status;// "Online","Reviewing","In-Game","Invited"

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlayerVO(String playerID, String uname, String status) {
			this.playerID = playerID;
			this.uname = uname;
			this.status = status;
		}
	}