package commons;

public class ServerMessage {
    public enum Type{
        NEW_SINGLEPLAYER_GAME, NEW_MULTIPLAYER_GAME, TEST
    }

    public Type type;
    // add more fields as we start exchanging messages

    public Question question;
    public int score;

    public ServerMessage() {
    }

    public ServerMessage(Type type) {
        this.type = type;
    }


}
