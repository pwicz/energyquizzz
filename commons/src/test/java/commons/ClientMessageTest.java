package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientMessageTest {
    @Test
    public void testConstructor(){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.INIT_MULTIPLAYER, 23L, 499L);

        assertEquals(ClientMessage.Type.INIT_MULTIPLAYER, msg.type);
        assertEquals(23L, msg.playerID);
        assertEquals(499L, msg.gameID);
    }
}
