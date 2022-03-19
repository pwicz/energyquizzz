package commons;

import java.util.List;

public class ServerMessage {
    public enum Type{
        NEW_SINGLEPLAYER_GAME, NEW_MULTIPLAYER_GAME, TEST, LOAD_NEW_QUESTIONS, DISPLAY_ANSWER, DISPLAY_INBETWEENSCORES
    }

    public Type type;
    // add more fields as we start exchanging messages

    public Question question;
    public int score;
    public String gameID;
    public double timerFull;
    public double timerFraction;

    public ServerMessage() {
    }

    public ServerMessage(Type type) {
        this.type = type;
    }


}
