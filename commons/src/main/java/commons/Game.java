package commons;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    private List<Player> players;
    private int playersAnswered = 0;
    private String ID;
    private boolean isMultiplayer;
    private int questionCounter = 0;
    private long correctAnswerID;
    private int round;
    private boolean hasEnded;
    private long questionStartTime;
    private TimerTask endOfQuestionTask;

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

    public void setQuestionCounter(int questionCounter) {
        this.questionCounter = questionCounter;
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
}
