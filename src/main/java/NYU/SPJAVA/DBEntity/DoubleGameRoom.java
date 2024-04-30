package NYU.SPJAVA.DBEntity;


public class DoubleGameRoom {
    private int hostPlayerID; //player ID of host player
    private int invitedPlayerID; //player ID of invited player
    private String word; //word for this game
    private int hostScore; // score host got

    public int getHostPlayerID() {
        return hostPlayerID;
    }

    public void setHostPlayerID(int hostPlayerID) {
        this.hostPlayerID = hostPlayerID;
    }

    public int getInvitedPlayerID() {
        return invitedPlayerID;
    }

    public void setInvitedPlayerID(int invitedPlayerID) {
        this.invitedPlayerID = invitedPlayerID;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getHostScore() {
        return hostScore;
    }

    public void setHostScore(int hostScore) {
        this.hostScore = hostScore;
    }

    public int getInvitedScore() {
        return invitedScore;
    }

    public void setInvitedScore(int invitedScore) {
        this.invitedScore = invitedScore;
    }

    public DoubleGameRoom(int hostPlayerID, int invitedPlayerID, String word, int hostScore, int invitedScore) {
        this.hostPlayerID = hostPlayerID;
        this.invitedPlayerID = invitedPlayerID;
        this.word = word;
        this.hostScore = hostScore;
        this.invitedScore = invitedScore;
    }

    private int invitedScore; //score the other player got

}
