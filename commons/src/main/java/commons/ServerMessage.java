package commons;

public class ServerMessage {
    public enum Type{
        NEW_SINGLEPLAYER_GAME, NEW_MULTIPLAYER_GAME
    }

    public Type type;
    // add more fields as we start exchanging messages

    public ServerMessage(Type type) {
        this.type = type;
    }
}
