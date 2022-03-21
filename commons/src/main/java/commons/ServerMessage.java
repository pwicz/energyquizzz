package commons;

public class ServerMessage {
    public enum Type{
        NEW_SINGLEPLAYER_GAME, NEW_MULTIPLAYER_GAME, TEST, NEXT_QUESTION, RESULT, END
    }

    public Type type;
    // add more fields as we start exchanging messages

    public Question question;
    public int score;
    public double timerFull;
    public double timerFraction;
    public String gameID;
    public Long correctAnswerID;
    public Long pickedAnswerID;
    public int round;

    public ServerMessage() {
    }

    public ServerMessage(Type type) {
        this.type = type;
    }


}
