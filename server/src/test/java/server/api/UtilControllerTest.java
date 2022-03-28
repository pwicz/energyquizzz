package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UtilControllerTest {

    private UtilController utilController;

    @BeforeEach
    public void setup(){
        utilController = new UtilController();
    }

    /**
     * Tests that the player ID is correctly received and is not null
     */
    @Test
    void testNotNull() {
        assertNotNull(utilController.getPlayerID());
    }

    /**
     * Tests if multiple newly generated IDs are unique
     */
    @Test
    void testUnique() {
        List<String> ids = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            ids.add(utilController.getPlayerID());
        }
        for(int i = 0; i < ids.size(); i++){
            for(int j = 0; i < ids.size(); i++){
                if(i != j){
                    assertNotEquals(ids.get(i), ids.get(j));
                }
            }
        }
    }

}