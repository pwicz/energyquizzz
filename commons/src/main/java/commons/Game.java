package commons;

import java.util.List;
import java.util.Objects;
import java.util.Timer;

public class Game {

    private String ID;
    private List<Player> players;
    private int playersAnswered = 0;
    private int questionCounter = 0;
    private Question currentQuestion;
    private long correctAnswerID;
    private int round;
    private boolean hasEnded;
    private boolean isMultiplayer;
    private boolean acceptsAnswers;
    private long questionStartTime;
    private Question.Type type;

    public Timer questionEndAction;

    public Game(List<Player> players, String ID) {
        this.players = players;
        this.ID = ID;

        this.round = 1;
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public Player getPlayerWithID(String playerID){
        for(var p : players){
            if(Objects.equals(p.getID(), playerID)) return p;
        }

        return null;
    }

    public Long getCorrectAnswerID() {
        return correctAnswerID;
    }

    public void setCorrectAnswerID(Long correctAnswerID) {
        this.correctAnswerID = correctAnswerID;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public void setHasEnded(boolean hasEnded) {
        this.hasEnded = hasEnded;
    }

    public int getQuestionCounter() {
        return questionCounter;
    }

    public int incQuestionCounter() {
        this.questionCounter = questionCounter + 1;
        return questionCounter;
    }

    public Question.Type getType() {
        return type;
    }

    public void setType(Question.Type type) {
        this.type = type;
    }

    public long getQuestionStartTime() {
        return questionStartTime;
    }

    public void setQuestionStartTime(long questionStartTime) {
        this.questionStartTime = questionStartTime;
    }

    public int getPlayersAnswered() {
        return playersAnswered;
    }

    public void setPlayersAnswered(int playersAnswered) {
        this.playersAnswered = playersAnswered;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean acceptsAnswers() {
        return acceptsAnswers;
    }

    public void setAcceptsAnswers(boolean acceptsAnswers) {
        this.acceptsAnswers = acceptsAnswers;
    }
}
