package server.api;

import commons.Quote;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    /**
     * Returns all questions stored in the database.
     * @return all questions stored in the database
     */
    @GetMapping("/")
    public List<String> getAll(){
        List<String> questions = new ArrayList<>();
        questions.add("Question 1");
        questions.add("Question 2");
        questions.add("Question 3");

        return questions;
    }

    /**
     * Returns {number} randomly chosen questions stored in the database.
     * @return {number} randomly chosen questions stored in the database
     */
    @GetMapping("/random/{number}")
    public List<String> getRandomlyChosen(@PathVariable("number") int num){
        List<String> questions = new ArrayList<>();
        questions.add("Random question 1/"+num);
        questions.add("Random question 2/"+num);
        questions.add("Random question 3/"+num);

        return questions;
    }

    /**
     * Returns a question with specified {id} from the database.
     * @param id id of the question to be returned
     * @return a question with specified {id} from the database.
     */
    @GetMapping("/{id}")
    public String getSpecificQuestion(@PathVariable("id") long id){
        return "Question with ID " + id;
    }

    /**
     * Adds a question sent in the response to the database.
     * @param question question to be added in the database
     * @return an error if the question was not properly formatted or a question just added to the database
     */
    @PostMapping("/")
    public ResponseEntity<String> addQuestion(@RequestBody String question){
        // some validation...
        // question being added to the db...
        return ResponseEntity.ok("Question that was just placed in the DB");
    }

    /**
     * Removes a specified question from the database.
     * @param id id of a question to be removed from the database
     * @return an error if the question was not found or the removed question
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable("id") long id){
        // deletion from the db
        return ResponseEntity.ok("Question with ID: " + id + " that was just deleted from the DB");
    }
}
