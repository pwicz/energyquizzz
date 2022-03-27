package server.api;

import commons.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ActivityRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final Random random;
    private final ActivityRepository repo;

    public ActivityController(Random random, ActivityRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Returns all activities stored in the database.
     * @return all activities stored in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Activity> getAll(){
        return repo.findAll();
    }

    /**
     * Returns a randomly chosen activity stored in the database.
     * @return a randomly chosen activity stored in the database
     */
    @GetMapping("/random")
    public ResponseEntity<Activity> getRandom(){
        long count = repo.count();
        if(count == 0L) return ResponseEntity.notFound().build();
        int index = random.nextInt((int)count);

        // divide all activites into '1-activity' pages and select a random page
        Page<Activity> activityPage = repo.findAll(PageRequest.of(index, 1));
        Activity q = null;
        if(activityPage.hasContent()) q = activityPage.getContent().get(0);

        return ResponseEntity.ok(q);
    }

    /**
     * Returns an activity with specified {id} from the database.
     * @param id id of the activity to be returned
     * @return an activity with specified {id} from the database.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getSpecificActivity(@PathVariable("id") long id){
        Optional<Activity> activityToFind = repo.findById(id);
        if(activityToFind.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(activityToFind.get());
    }

    /**
     * Adds an activity sent in the response to the database.
     * @param activity activity to be added in the database
     * @return an error if the activity was not properly formatted or an activity just added to the database
     */
    @PutMapping(path = { "", "/"})
    public ResponseEntity<Activity> addActivity(@RequestBody Activity activity){
        // data validation

        if(activity.title == null
                || activity.consumptionInWh <= 0
                || activity.source == null
                || activity.imagePath == null)
        {
            return ResponseEntity.badRequest().build();
        }

        Activity saved = repo.save(activity);
        return ResponseEntity.ok(saved);
    }

    /**
     * Edits the activity with a given id and replaces it with the properties of the new activity
     * @param id id of the activity to edit
     * @param newActivity properties of the new activity
     * @return the newly edited activity
     */
    @PutMapping(path = { "", "/edit/{id}"})
    @Transactional
    public Activity editActivity(@PathVariable long id, @RequestBody Activity newActivity){
        // data validation
        if(newActivity.title == null
                || newActivity.consumptionInWh <= 0
                || newActivity.source == null
                || newActivity.imagePath == null)
        {
            throw new IllegalArgumentException();
        }

        Activity old = repo.findById(id).orElseThrow(
                () -> new IllegalStateException("Activity with ID " + id + " does not exist")
        );

        old.title = newActivity.title;
        old.consumptionInWh = newActivity.consumptionInWh;
        old.source = newActivity.source;
        old.imagePath = newActivity.imagePath;

        return old;
    }

    /**
     * Removes a specified activity from the database.
     * @param id id of an activity to be removed from the database
     * @return an error if the activity was not found or the removed activity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Activity> deleteActivity(@PathVariable("id") long id){

        Optional<Activity> activityToRemove = repo.findById(id);
        if(activityToRemove.isEmpty()) return ResponseEntity.notFound().build();

        repo.deleteById(id);
        if(repo.findById(id).isEmpty()) System.out.println("Successfully deleted activity " + id);
        else System.out.println("Activity " + id + " was not deleted");
        System.out.println(repo.findById(id));
        return ResponseEntity.ok(activityToRemove.get());
    }
}
