package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.*;


@Entity
//@Table (name = "SCORE")
public class Score {

    @Id
    @SequenceGenerator(
            name = "score_sequence",
            sequenceName = "score_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "score_sequence"
    )
    public long id;

    //@Column (name="playerName")
    public String playerName;

    //@Column (name="playerScore")
    public int playerScore;

    public Score(String playerName, int playerScore) {
        this.playerName = playerName;
        this.playerScore = playerScore;
    }

    public Score() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score = (Score) o;

        if (id != score.id) return false;
        if (playerScore != score.playerScore) return false;
        return playerName != null ? playerName.equals(score.playerName) : score.playerName == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (playerName != null ? playerName.hashCode() : 0);
        result = 31 * result + playerScore;
        return result;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", playerScore=" + playerScore +
                '}';
    }


}
