package server.api;

import commons.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    }

    private static  Score getScore(int score){
        String player = "player";
        return new Score( player, score);
    }
}