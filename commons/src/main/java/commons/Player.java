package commons;

public class Player {

    private boolean hasAnswered;
    private String name;
    private String ID;
    private int score = 0;
    private int recentlyReceivedPoints = 0;
    private long answer;
    private boolean answerStatus = false;

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

    public int getRecentlyReceivedPoints() {
        return recentlyReceivedPoints;
    }

    public void setRecentlyReceivedPoints(int recentlyReceivedPoints) {
        this.recentlyReceivedPoints = recentlyReceivedPoints;
    }
}
