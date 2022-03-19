package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientMessageTest {
    @Test
    public void testConstructor(){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.INIT_MULTIPLAYER, "23", "433");

        assertEquals(ClientMessage.Type.INIT_MULTIPLAYER, msg.type);
        assertEquals("23", msg.playerID);
        assertEquals("433", msg.gameID);
    }
}
