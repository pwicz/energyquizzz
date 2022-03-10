package server.api;

import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestionControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestQuestionRepository repo;

    private QuestionController sut;

    @BeforeEach
    public void setup(){
        random = new MyRandom();
        repo = new TestQuestionRepository();

        sut = new QuestionController(random, repo);
    }

    @Test
    public void testGetAll(){
        List<Question> expected = new ArrayList<>();
        expected.add(getQuestion("q1"));
        expected.add(getQuestion("q2"));

        sut.addQuestion(expected.get(0));
        sut.addQuestion(expected.get(1));

        var actual = sut.getAll();
        assertEquals(expected, actual);

        List<String> expectedOperations = List.of("save", "save", "findAll");
        assertEquals(expectedOperations, repo.calledMethods);
    }

    @Test
    public void testGetRandom(){
        Question expected = getQuestion("q1");

        sut.addQuestion(getQuestion("q9"));
        sut.addQuestion(expected);
        sut.addQuestion(getQuestion("q5"));

        nextInt = 1;
        repo.calledMethods.clear();
        Question actual = sut.getRandom().getBody();

        assertEquals(expected, actual);
        assertTrue(random.wasCalled);
        assertEquals(List.of("count", "findAll"), repo.calledMethods);
    }

    @Test
    public void testGetSpecificQuestion(){
        Question expected = getQuestion("q1");
        expected.id = 12L;

        Question other = getQuestion("q42");
        other.id = 6L;

        sut.addQuestion(expected);
        sut.addQuestion(other);
        repo.calledMethods.clear();

        assertEquals(expected, sut.getSpecificQuestion(12L).getBody());
        assertEquals(List.of("findById"), repo.calledMethods);
    }

    @Test
    public void testGetSpecificQuestionNotFound(){
        Question q1 = getQuestion("q1");
        q1.id = 12L;
        Question q2 = getQuestion("q42");
        q2.id = 6L;
        sut.addQuestion(q1);
        sut.addQuestion(q2);
        repo.calledMethods.clear();

        assertNull(sut.getSpecificQuestion(9L).getBody());
        assertEquals(List.of("findById"), repo.calledMethods);
    }

    @Test
    public void testAddQuestion(){
        Question q1 = new Question("Title to test", 420, "source::link", "image::path");
        q1.id = 11L;
        sut.addQuestion(q1);
        assertEquals(List.of("save"), repo.calledMethods);

        Question actual = sut.getSpecificQuestion(11L   ).getBody();

        assertNotNull(actual);
        assertEquals( 11L, actual.id);
        assertEquals( "Title to test", actual.title);
        assertEquals( 420, actual.consumptionInWh);
        assertEquals("source::link", actual.source);
        assertEquals("image::path", actual.imagePath);

    }

    @Test
    public void testDeleteQuestion(){
        Question expected = getQuestion("qqq2");
        expected.id = 11L;
        sut.addQuestion(expected);

        Question actual = sut.deleteQuestion(11L).getBody();

        assertEquals(expected, actual);
        assertNull(sut.getSpecificQuestion(11L).getBody());
    }

    private static Question getQuestion(String data){
        return new Question(data, 420, data, data);
    }

    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}
