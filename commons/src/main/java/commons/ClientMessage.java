package commons;

public class ClientMessage {
    public enum Type{
        INIT_SINGLEPLAYER, INIT_MULTIPLAYER, TEST
    }

    public Type type;
    public Long playerID;
    public Long gameID;
    // add more fields as we start exchanging messages

    public String playerName;

    public ClientMessage() {
    }

    public ClientMessage(Type type, Long playerID, Long gameID) {
        this.type = type;
        this.playerID = playerID;
        this.gameID = gameID;
    }
}
