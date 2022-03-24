package commons;

public class ClientMessage {
    public enum Type{
        INIT_SINGLEPLAYER, INIT_MULTIPLAYER, TEST, SUBMIT_SINGLEPLAYER, QUIT,
        INIT_QUESTION, SUBMIT_ANSWER, START_GAME, QUIT_WAITING_ROOM
    }

    public Type type;
    public String playerID;
    public String gameID;
    public double time;
    public long chosenActivity;
    // add more fields as we start exchanging messages
    public String playerName;
    
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
