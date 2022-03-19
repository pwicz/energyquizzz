package commons;

public class ClientMessage {
    public enum Type{
        INIT_SINGLEPLAYER, INIT_MULTIPLAYER, TEST, INIT_QUESTION
    }

    public Type type;
    public String playerID;
    public String gameID;
    // add more fields as we start exchanging messages

    public ClientMessage() {
    }

    public ClientMessage(Type type, String playerID, String gameID) {
        this.type = type;
        this.playerID = playerID;
        this.gameID = gameID;
    }
}
