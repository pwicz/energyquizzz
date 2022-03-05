package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoreTest {
    private static final String PLAYERNAME = "Peter";
    private static final int PLAYERSCORE = 9999;

    @Test
    public void testConstructor(){
        Score s = new Score(PLAYERNAME, PLAYERSCORE);
        assertEquals(PLAYERNAME, s.playerName);
        assertEquals(PLAYERSCORE, s.playerScore);
    }

    @Test
    public void testEqualHashCode(){
        Score s1 = new Score(PLAYERNAME, PLAYERSCORE);
        Score s2 = new Score(PLAYERNAME, PLAYERSCORE);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testNotEqualHashCode(){
        Score s1 = new Score(PLAYERNAME + "a", PLAYERSCORE + 12);
        Score s2 = new Score(PLAYERNAME, PLAYERSCORE);

        assertNotEquals(s1, s2);
        assertNotEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testToString(){
        Score s = new Score(PLAYERNAME, PLAYERSCORE);
        String scoreString = s.toString();

        assertTrue(scoreString.contains(Score.class.getSimpleName()));
        assertTrue(scoreString.contains(PLAYERNAME));
        assertTrue(scoreString.contains(Integer.toString(PLAYERSCORE)));
    }
}
