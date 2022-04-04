package commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    public List<Activity> activities;
    public Type type;
    public String title;
    public List<Long> options;

    private long correct;
    private List<Long> incorrect;

    public enum Type{
        COMPARE,
        GUESS,
        HOW_MANY_TIMES,
        ESTIMATION
    }

    public Question() {
        activities = new ArrayList<>();
    }

    /**
     * Constructor for compare & estimation questions
     * @param activities selected activities to formulate question
     * @param type type of question
     */
    public Question(List<Activity> activities, Type type) {
        this.activities = activities;
        this.type = type;
        this.title = getTitleFromType(type);
        this.incorrect = new ArrayList<>();

        //Selects correct & incorrect answers for compare type questions
        if (type == Type.COMPARE) {
            for(Activity a : activities){
                if(this.correct < a.consumptionInWh) this.correct = a.consumptionInWh;
            }
            for(Activity a : activities){
                if(this.correct != a.consumptionInWh) this.incorrect.add((long) a.consumptionInWh);
            }
        }
    }

    /**
     * Constructor for guessing & how many times type questions
     * @param activities activity selected
     * @param type question type (Guess or how many times)
     * @param options three values (only one correct)
     */
    public Question(List<Activity> activities, Type type, List<Long> options) {
        this.activities = activities;
        this.type = type;
        this.title = getTitleFromType(type);
        if(type == Type.HOW_MANY_TIMES){
            title += activities.get(1).title + ", how many times could you do this activity?";
        }
        this.correct = options.get(0);
        this.options = options;
        //Add incorrect answer
        this.incorrect = new ArrayList<>();
        this.incorrect.add(options.get(1));
        this.incorrect.add(options.get(2));
        Collections.shuffle(this.options);
    }

    public static String getTitleFromType(Type type){
        if(type == Type.COMPARE) return "Which activity consumes most energy?";
        else if(type == Type.GUESS) return "How much energy does this activity take?";
        else if(type == Type.HOW_MANY_TIMES) return "Instead of ";
        else if(type == Type.ESTIMATION) return "How much energy does this activity take?";
        return null;
    }

    public void addActivity(Activity a){
        activities.add(a);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public long getCorrect(){
        return correct;
    }

    public List<Long> getIncorrect(){
        return incorrect;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Long> getOptions(){
        return this.options;
    }

    public void setOptions(List<Long> options){
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (activities != null ? !activities.equals(question.activities) : question.activities != null) return false;
        return type == question.type;
    }

    @Override
    public int hashCode() {
        int result = activities != null ? activities.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Question{" +
                "activities=" + activities +
                ", type=" + type +
                '}';
    }
}
