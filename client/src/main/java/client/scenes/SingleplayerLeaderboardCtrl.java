package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SingleplayerLeaderboardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    Button start;

    @FXML
    Button leave;

    @FXML
    TextField nameField;

    @FXML
    ListView<String> leaderboard;



    @Inject
    public SingleplayerLeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void initialize() {
        start.fire(); //this loads the leaderboard, it is currently bound to the start button

    }


    public void start() { //start a singleplayer game
        System.out.println("test");
    }

    public void insertName() {
        String name =nameField.getText();
        System.out.println(name); // You need to press enter when inserting you name.
    }

    public void insertLeaderboard() throws MalformedURLException { //needs to change to import the database leaderboard
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<int[]> response = restTemplate.getForEntity("http://localhost:8080/api/scores/get5TopScores",
                int[].class);
        int[] scores = response.getBody();
        ArrayList<String> topScores = new ArrayList<>();
        for(int i = 0; i<scores.length;i++) {
            topScores.add(String.valueOf(scores[i]));
        }
        leaderboard.getItems().addAll(topScores);
    }

    public void leave(){
        mainCtrl.showSplash();
    }
}
