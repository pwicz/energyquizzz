package commons;

import java.util.UUID;

public class Player {

    private boolean hasAnswered;
    private String name;
    private String ID;
    private int score = 0;
    private int scoreModifier = 1;
    private long answer;
    private boolean answerStatus = false;

    private UUID timerID;

    public Player(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean hasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    public boolean isHasAnswered() {
        return hasAnswered;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(long answer) {
        this.answer = answer;
    }

    public void setAnswerStatus(boolean status){
        answerStatus = status;
    }

    public boolean getAnswerStatus(){
        return answerStatus;
    }

    public void setScoreModifier(int modifier){ this.scoreModifier = modifier; }

    public int getScoreModifier(){ return scoreModifier; }

    public UUID getTimerID() {
        return timerID;
    }

    public void setTimerID(UUID timerID) {
        this.timerID = timerID;
    }
}
