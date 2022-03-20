/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;
import java.util.List;
import java.util.Optional;

import commons.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ScoreRepository;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreRepository repo;

    //make it get scores from database v
    public ScoreController(ScoreRepository repo) {
        this.repo = repo;
        //Hardcoded scores for testing purposes
        repo.save(new Score("Player 1", 500));
        repo.save(new Score("Player 2", 100));
        repo.save(new Score("Player 3", 1500));
        repo.save(new Score("Player 4", 0));
        repo.save(new Score("Player 5", 750));
    }

    /**
     * Returns all scores stored in the database.
     * @return all scores stored in the database
     */
    @GetMapping(path = {"","/"})
    public List<Score> getAllScores() {
        return repo.findAll();
    }

    /*@GetMapping("/getScoreOf{playerName}")
    public Integer getScoreAt(@PathVariable("playerName") String playerName) {
        return playerScores.get(playerName);
    }*/

    @GetMapping("/getTop{number}Scores")
    public ResponseEntity<List<Score>> getTopScores(@PathVariable("number") int number){
        Page<Score> page = repo.findAll(PageRequest.of(0, number, Sort.by(Sort.Order.desc("playerScore"))));
        var result = page.getContent();

        if(result.size() == 0) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = { "", "/"})
    public ResponseEntity<Score> addScore(@RequestBody Score score){

        // data validation
        if(score.playerName == null || score.playerName.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Score saved = repo.save(score);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Score> deleteScore(@PathVariable("id") long id){

        Optional<Score> scoreToRemove = repo.findById(id);
        if(scoreToRemove.isEmpty()) return ResponseEntity.notFound().build();

        repo.deleteById(id);
        return ResponseEntity.ok(scoreToRemove.get());
    }

}