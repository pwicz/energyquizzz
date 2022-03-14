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

import java.sql.*;
import java.util.*;

import commons.Score;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ScoreRepository;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    //private Map<String, Integer> playerScores;
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
    }*/

    @GetMapping("/get{number}TopScores")
    public List<Integer> getTopScores(@PathVariable("number") int number) throws SQLException {
        List<Integer> topScores = new ArrayList<>();
        /*for(String key: playerScores.keySet()) {
            topScores.add(playerScores.get(key));
        }*/
        String url="jdbc:h2:file:./quizzzz";
        Connection conn = DriverManager.getConnection(url,"","");
        Statement stmt= conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT playerScore FROM score ORDER BY playerScore DESC");
        while(rs.next()){
            topScores.add(rs.getInt("playerScore"));
        }
        // Collections.sort(topScores);
        List<Integer> res = new ArrayList<>();
        for(int i = 0; i < number; i++){
            res.add(topScores.get(i));
        }
        // Collections.reverse(res);
         return res;
    }





}