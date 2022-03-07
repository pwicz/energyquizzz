package server.api;

import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
