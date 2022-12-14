package commons;

import java.util.List;

public class ServerMessage {


    public enum Type{
        NEW_SINGLEPLAYER_GAME, NEW_MULTIPLAYER_GAME, TEST, NEXT_QUESTION, RESULT, END,
        LOAD_NEW_QUESTIONS, DISPLAY_ANSWER, DISPLAY_INBETWEENSCORES, END_GAME, INIT_PLAYER,
        EXTRA_PLAYER, PING, SHOW_EMOJI, NAME_TAKEN, LOCK_ANSWER, UPDATE_TIMER, REMOVE_ANSWER,
        JOKER_USED
    }

    public Type type;
    // add more fields as we start exchanging messages

    public ClientMessage.Joker jokerType;
    public String jokerUsedBy;

    public Question.Type typeQ;

    public Question question;
    public int score;
    public double timerFull;
    public double timerFraction;
    public String gameID;
    public int round;
    public List<String> correctlyAnswered;
    public List<String> incorrectlyAnswered;

    public List<Score> topScores;
    public List<String> playersWaiting;
    public long pickedID;
    public long correctID;
    public long incorrectID;
    public int questionCounter;
    public int totalQuestions;
    public String imgName;
    public String namePLayerEmoji;

    public String playerName;

    public boolean answeredCorrect;
    public int receivedPoints;

    public ServerMessage() {
    }

    public ServerMessage(Type type) {
        this.type = type;
    }


}
