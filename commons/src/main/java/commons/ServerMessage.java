package commons;

import java.util.List;

public class ServerMessage {


    public enum Type{
        NEW_SINGLEPLAYER_GAME, NEW_MULTIPLAYER_GAME, TEST, NEXT_QUESTION, RESULT, END,
        LOAD_NEW_QUESTIONS, DISPLAY_ANSWER, DISPLAY_INBETWEENSCORES, END_GAME, INIT_PLAYER,
        EXTRA_PLAYER
    }

    public Type type;
    // add more fields as we start exchanging messages

    public Question question;
    public int score;
    public double timerFull;
    public double timerFraction;
    public String gameID;
    public Long correctAnswerID;
    public Long pickedAnswerID;
    public int round;
    public List<String> correctlyAnswered;
    public List<String> incorrectlyAnswered;

    public List<String> topScores;
    public List<String> playersWaiting;
    public long pickedID;
    public long correctID;
    public int questionCounter;
    public int totalQuestions;
    public ServerMessage() {
    }

    public ServerMessage(Type type) {
        this.type = type;
    }


}
