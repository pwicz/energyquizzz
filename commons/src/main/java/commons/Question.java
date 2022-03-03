package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Objects;

@Entity
public class Question {

    @Id
    @SequenceGenerator(
            name = "question_sequence",
            sequenceName = "question_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "question_sequence"
    )
    public long id;

    public String title;
    public Integer consumptionInWh;
    public String source;
    public String imagePath;

    public Question(String title, Integer consumptionInWh, String source, String imagePath) {
        this.title = title;
        this.consumptionInWh = consumptionInWh;
        this.source = source;
        this.imagePath = imagePath;
    }

    public Question() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (id != question.id) return false;
        if (!Objects.equals(title, question.title)) return false;
        if (!Objects.equals(consumptionInWh, question.consumptionInWh))
            return false;
        if (!Objects.equals(source, question.source)) return false;
        return Objects.equals(imagePath, question.imagePath);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (consumptionInWh != null ? consumptionInWh.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", consumptionInWh=" + consumptionInWh +
                ", source='" + source + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
