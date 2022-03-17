package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class SingleplayerLeaderboardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private int counter = 0;

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
        counter++;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<int[]> response = restTemplate.getForEntity("http://localhost:8080/api/scores/get"
                        + counter + "TopScores", int[].class);
        int[] scores = response.getBody();
        ArrayList<String> topScores = new ArrayList<>();
        for(int i = 0; i<scores.length;i++) {
            topScores.add(String.valueOf(scores[i]));
        }
        System.out.println(topScores.toString());
        leaderboard.getItems().setAll(topScores);
    }

    public void leave(){
        mainCtrl.showSplash();
    }
}
