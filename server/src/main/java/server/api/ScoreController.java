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

import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private Map<String, Integer> playerScores;

    public ScoreController() {
        this.playerScores = new HashMap<>();
        playerScores.put("Player 1", 1250);
        playerScores.put("Player 2", 500);
        playerScores.put("Player 3", 1500);
        playerScores.put("Player 4", 700);
        playerScores.put("Player 5", 0);
    }

    @GetMapping("/getAllScores")
    public Map<String, Integer> getAllScores() {
        return playerScores;
    }

    @GetMapping("/getScoreOf{playerName}")
    public Integer getScoreAt(@PathVariable("playerName") String playerName) {
        return playerScores.get(playerName);
    }

    @GetMapping("/get{number}TopScores")
    public List<Integer> getTopScores(@PathVariable("number") int number) {
        List<Integer> topScores = new ArrayList<>();
        for(String key: playerScores.keySet()) {
            topScores.add(playerScores.get(key));
        }
        Collections.sort(topScores);
        List<Integer> res = new ArrayList<>();
        for(int i = 0; i < number; i++){
            res.add(topScores.get(i));
        }
        Collections.reverse(res);
        return res;
    }
}