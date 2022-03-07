package server.api;

import commons.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.QuestionRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final Random random;
    private final QuestionRepository repo;

    public QuestionController(Random random, QuestionRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Returns all questions stored in the database.
     * @return all questions stored in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Question> getAll(){
        return repo.findAll();
    }

    /**
     * Returns a randomly chosen question stored in the database.
     * @return a randomly chosen question stored in the database
     */
    @GetMapping("/random")
    public ResponseEntity<Question> getRandom(){
        if(repo.count() == 0) return ResponseEntity.notFound().build();
        int index = random.nextInt((int)repo.count());

        // divide all questions into '1-question' pages and select a random page
        Page<Question> questionPage = repo.findAll(PageRequest.of(index, 1));
        Question q = null;
        if(questionPage.hasContent()) q = questionPage.getContent().get(0);

        return ResponseEntity.ok(q);
    }

    /**
     * Returns a question with specified {id} from the database.
     * @param id id of the question to be returned
     * @return a question with specified {id} from the database.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getSpecificQuestion(@PathVariable("id") long id){
        Optional<Question> questionToFind = repo.findById(id);
        if(questionToFind.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(questionToFind.get());
    }

    /**
     * Adds a question sent in the response to the database.
     * @param question question to be added in the database
     * @return an error if the question was not properly formatted or a question just added to the database
     */
    @PostMapping("/")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question){
        // data validation

        if(question.title == null
                || question.consumptionInWh <= 0
                || question.source == null
                || question.imagePath == null)
        {
            return ResponseEntity.badRequest().build();
        }

        Question saved = repo.save(question);
        return ResponseEntity.ok(saved);
    }

    /**
     * Removes a specified question from the database.
     * @param id id of a question to be removed from the database
     * @return an error if the question was not found or the removed question
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Question> deleteQuestion(@PathVariable("id") long id){

        Optional<Question> questionToRemove = repo.findById(id);
        if(questionToRemove.isEmpty()) return ResponseEntity.notFound().build();

        repo.deleteById(id);
        return ResponseEntity.ok(questionToRemove.get());
    }
}
