package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestionTest {
    @Test
    public void testConstructor(){
        Question q = new Question(new ArrayList<>(), Question.Type.COMPARE);

        assertEquals(Question.Type.COMPARE, q.getType());
    }

    @Test
    public void equalsHashCode(){
        Question q1 = new Question(new ArrayList<>(), Question.Type.COMPARE);
        Question q2 = new Question(new ArrayList<>(), Question.Type.COMPARE);

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void notEqualsHasCode(){
        Question q1 = new Question(new ArrayList<>(), Question.Type.COMPARE);
        Question q2 = new Question(new ArrayList<>(), Question.Type.ESTIMATION);

        assertNotEquals(q1, q2);
        assertNotEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void testToString(){
        Question q = new Question(new ArrayList<>(), Question.Type.COMPARE);
        String questionString = q.toString();

        assertTrue(questionString.contains(Question.class.getSimpleName()));
        assertTrue(questionString.contains("COMPARE"));
    }
}
