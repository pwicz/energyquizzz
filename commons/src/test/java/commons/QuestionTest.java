package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class QuestionTest {
    private static final String TITLE = "Taking a hot shower for 6 minutes";
    private static final int CONSUMPTIONINWH = 4000;
    private static final String SOURCE = "https://somelink.com";
    private static final String IMAGEPATH = "https://source.com";

    @Test
    public void testConstructor(){
        Question q = new Question(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);

        assertEquals(TITLE, q.title);
        assertEquals(CONSUMPTIONINWH, q.consumptionInWh);
        assertEquals(SOURCE, q.source);
        assertEquals(IMAGEPATH, q.imagePath);
    }

    @Test
    public void equalsHashCode(){
        Question q1 = new Question(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);
        Question q2 = new Question(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);

        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void notEqualsHasCode(){
        Question q1 = new Question(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);
        Question q2 = new Question(TITLE + " and something", CONSUMPTIONINWH + 4502, SOURCE, IMAGEPATH);

        assertNotEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void testToString(){
        Question q1 = new Question(TITLE, CONSUMPTIONINWH, SOURCE, IMAGEPATH);
        String questionString = q1.toString();

        assertTrue(questionString.contains(Question.class.getSimpleName()));
        assertTrue(questionString.contains(TITLE));
        assertTrue(questionString.contains(SOURCE));
        assertTrue(questionString.contains(IMAGEPATH));
    }
}
