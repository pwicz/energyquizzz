package commons;

public class ClientMessage {
    public enum Type{
        INIT_SINGLEPLAYER, INIT_MULTIPLAYER, TEST, SUBMIT_SINGLEPLAYER, QUIT
    }

    public Type type;
    public String playerID;
    public String gameID;
    // add more fields as we start exchanging messages

    public String playerName;
    public Long chosenActivity;
    public double time;
    public ClientMessage() {
    }

    public ClientMessage(Type type, String playerID, String gameID) {
        this.type = type;
        this.playerID = playerID;
        this.gameID = gameID;
    }
}
