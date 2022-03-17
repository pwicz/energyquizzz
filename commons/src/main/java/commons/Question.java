package commons;

import java.util.ArrayList;
import java.util.List;

public class Question {
    public List<Activity> activities;
    public Type type;

    public enum Type{
        COMPARE,
        GUESS,
        HOW_MANY_TIMES,
        ESTIMATION
    }

    public Question() {
        activities = new ArrayList<>();
    }

    public Question(List<Activity> activities, Type type) {
        this.activities = activities;
        this.type = type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
