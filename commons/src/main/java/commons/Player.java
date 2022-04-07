package commons;

import java.util.Timer;
import java.util.UUID;

public class Player {

    private boolean hasAnswered;
    private String name;
    private String ID;
    private int score = 0;
    private int scoreModifier = 1;
    private int recentlyReceivedPoints = 0;
    private long answer;
    private boolean answerStatus = false;
    private long timePenaltyMs = 0;
    private Timer lockAnswerTimer;
    private UUID timerID;

    // jokers
    private boolean usedSplitTime = false;
    private boolean usedDoublePoints = false;
    private boolean usedCutAnswer = false;

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

    public long getTimePenalty() {
        return timePenaltyMs;
    }

    public void setTimePenalty(long timePenalty) {
        this.timePenaltyMs = timePenalty;
    }

    public Timer getLockAnswerTimer() {
        return lockAnswerTimer;
    }

    public void setLockAnswerTimer(Timer lockAnswerTimer) {
        this.lockAnswerTimer = lockAnswerTimer;
    }

    public boolean isUsedSplitTime() {
        return usedSplitTime;
    }

    public void setUsedSplitTime(boolean usedSplitTime) {
        this.usedSplitTime = usedSplitTime;
    }

    public boolean isUsedDoublePoints() {
        return usedDoublePoints;
    }

    public void setUsedDoublePoints(boolean usedDoublePoints) {
        this.usedDoublePoints = usedDoublePoints;
    }

    public boolean isUsedCutAnswer() {
        return usedCutAnswer;
    }

    public void setUsedCutAnswer(boolean usedCutAnswer) {
        this.usedCutAnswer = usedCutAnswer;
    }

    public int getRecentlyReceivedPoints() {
        return recentlyReceivedPoints;
    }

    public void setRecentlyReceivedPoints(int recentlyReceivedPoints) {
        this.recentlyReceivedPoints = recentlyReceivedPoints;
    }
}
