package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerMessageTest {
    @Test
    public void testConstructor(){
        ServerMessage msg = new ServerMessage(ServerMessage.Type.NEW_MULTIPLAYER_GAME);

        assertEquals(ServerMessage.Type.NEW_MULTIPLAYER_GAME, msg.type);
    }
}
