package NYU.SPJAVA.NetworkEntity;

// put the response json in an entity, can call corresponding getter function to get the score and comment
public class ChatGPTResponse {
    private String score;
    private String comment;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ChatGPTResponse(String score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Score: " + score + ", Comment: " + comment;
    }
}