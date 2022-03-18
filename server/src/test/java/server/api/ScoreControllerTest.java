package server.api;

import commons.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreControllerTest {
    public int nextInt;
    private TestScoreRepository repo;
    private ScoreController s;

    @BeforeEach
    public void setup(){
        repo = new TestScoreRepository();
        s = new ScoreController(repo);
    }

    @Test
    public void testGetAllScores(){
        List<Score> expected = new ArrayList<>();
        expected.add(getScore(432));
        expected.add(getScore(654));

        s.addScore(expected.get(0));
        s.addScore(expected.get(1));

        var actual = s.getAllScores();
        assertEquals(expected, actual);

        List<String> expectedOperations = List.of("save", "save", "findAll");
        assertEquals(expectedOperations, repo.calledMethods);

    }

    private static  Score getScore(int score){
        String player = "player";
        return new Score( player, score);
    }

    @Test
    public void testGetTopScores (){
        List<Score> expected = new ArrayList<>();
        expected.add(getScore(888));
        expected.add(getScore(654));

        s.addScore(getScore(432));
        s.addScore(expected.get(0));
        s.addScore(expected.get(1));

        var actual = s.getTopScores(2).getBody();
        assertEquals(expected, actual);

        List<String> expectedOperations = List.of("save", "save", "save", "findAll");
        assertEquals(expectedOperations, repo.calledMethods);

    }
}