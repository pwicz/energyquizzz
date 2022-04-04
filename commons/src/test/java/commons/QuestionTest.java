package commons;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestionTest {


    @Test
    public void testConstructorCompare(){
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("title1", 10, "url1", "path1"));
        activities.add(new Activity("title2", 20, "url2", "path2"));
        activities.add(new Activity("title3", 30, "url3", "path3"));
        Question q = new Question(activities, Question.Type.COMPARE);

        assertNotNull(q);
        assertEquals(Question.Type.COMPARE, q.getType());
        assertEquals(activities, q.getActivities());
        assertEquals(Question.getTitleFromType(Question.Type.COMPARE), q.title);
    }

    @Test
    public void testConstructorGuess(){
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("title1", 10, "url1", "path1"));
        activities.add(new Activity("title2", 20, "url2", "path2"));
        activities.add(new Activity("title3", 30, "url3", "path3"));
        List<Long> options = new ArrayList<>();
        options.add(1L);
        options.add(2L);
        options.add(3L);
        List<Long> incorrect = new ArrayList<>();
        incorrect.add(options.get(1));
        incorrect.add(options.get(2));
        Question q = new Question(activities, Question.Type.GUESS, options);

        assertNotNull(q);
        assertEquals(Question.Type.GUESS, q.getType());
        assertEquals(activities, q.getActivities());
        assertEquals(Question.getTitleFromType(Question.Type.GUESS), q.title);
        assertEquals(options, q.options);
        assertEquals(1, q.getCorrect());
        assertEquals(incorrect, q.getIncorrect());
    }

    @Test
    public void testConstructorHowManyTimes(){
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("title1", 10, "url1", "path1"));
        activities.add(new Activity("title2", 20, "url2", "path2"));
        activities.add(new Activity("title3", 30, "url3", "path3"));
        List<Long> newOptions = new ArrayList<>();
        newOptions.add(1L);
        newOptions.add(2L);
        newOptions.add(3L);
        List<Long> incorrect = new ArrayList<>();
        incorrect.add(newOptions.get(1));
        incorrect.add(newOptions.get(2));
        Question q = new Question(activities, Question.Type.HOW_MANY_TIMES, newOptions);

        assertNotNull(q);
        assertEquals(Question.Type.HOW_MANY_TIMES, q.getType());
        assertEquals(activities, q.getActivities());
        assertEquals(newOptions, q.options);
        assertEquals(1, q.getCorrect());
        assertEquals(incorrect, q.getIncorrect());
    }

    @Test
    public void testConstructorEstimation(){
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("title1", 10, "url1", "path1"));
        activities.add(new Activity("title2", 20, "url2", "path2"));
        activities.add(new Activity("title3", 30, "url3", "path3"));
        Question q = new Question(activities, Question.Type.ESTIMATION);

        assertNotNull(q);
        assertEquals(Question.Type.ESTIMATION, q.getType());
        assertEquals(activities, q.getActivities());
        assertEquals(Question.getTitleFromType(Question.Type.ESTIMATION), q.title);
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
