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
import commons.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ScoreRepository;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreRepository repo;

    //make it get scores from database v
    public ScoreController(ScoreRepository repo) {
        this.repo = repo;
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
    }*/ //delete this later**

    @GetMapping("/get{number}TopScores")
    public ResponseEntity<List<Score>> getTopScores(@PathVariable("number") int number){
        Page<Score> page = repo.findAll(PageRequest.of(0, number, Sort.by(Sort.Order.desc("playerScore"))));
        var result = page.getContent();

        if(result.size() == 0) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = { "", "/"})
    public ResponseEntity<Score> addScore(@RequestBody Score score){
        // data validation

//        if(question.title == null
//                || question.consumptionInWh <= 0
//                || question.source == null
//                || question.imagePath == null)
//        {
//            return ResponseEntity.badRequest().build();
//        }

        Score saved = repo.save(score);
        return ResponseEntity.ok(saved);
    }



}