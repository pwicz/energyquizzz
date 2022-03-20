package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ActivityTest {
    private static final String TITLE = "Taking a hot shower for 6 minutes";
    private static final int CONSUMPTIONINWH = 4000;
    private static final String SOURCE = "https://somelink.com";
    private static final String IMAGEPATH = "https://source.com";

    @Test
    public void testConstructor(){
        Activity q = new Activity(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);

        assertEquals(TITLE, q.title);
        assertEquals(CONSUMPTIONINWH, q.consumptionInWh);
        assertEquals(SOURCE, q.source);
        assertEquals(IMAGEPATH, q.imagePath);
    }

    @Test
    public void equalsHashCode(){
        Activity q1 = new Activity(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);
        Activity q2 = new Activity(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void notEqualsHasCode(){
        Activity q1 = new Activity(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);
        Activity q2 = new Activity(TITLE + " and something", CONSUMPTIONINWH + 4502, SOURCE, IMAGEPATH);

        assertNotEquals(q1, q2);
        assertNotEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void testToString(){
        Activity q1 = new Activity(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);
        String questionString = q1.toString();

        assertTrue(questionString.contains(Activity.class.getSimpleName()));
        assertTrue(questionString.contains(TITLE));
        assertTrue(questionString.contains(SOURCE));
        assertTrue(questionString.contains(IMAGEPATH));
    }
}
