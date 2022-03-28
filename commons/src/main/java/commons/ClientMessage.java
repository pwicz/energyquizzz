package commons;

public class ClientMessage {
    public enum Type{
        INIT_SINGLEPLAYER, INIT_MULTIPLAYER, TEST, SUBMIT_SINGLEPLAYER, QUIT,
        SUBMIT_ANSWER, START_MULTIPLAYER, QUIT_WAITING_ROOM, PING
    }

    public Type type;
    public String playerID;
    public String gameID;
    public double time;
    public long chosenActivity;
    // add more fields as we start exchanging messages
    public String playerName;
    public String serverName;
    
    public ClientMessage() {
    }

    public ClientMessage(Type type) {
        this.type = type;
    }

    public ClientMessage(Type type, String playerID, String gameID) {
        this.type = type;
        this.playerID = playerID;
        this.gameID = gameID;
    }
}
