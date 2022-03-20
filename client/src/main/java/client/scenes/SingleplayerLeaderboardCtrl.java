package client.scenes;

import client.utils.ServerUtils;
import commons.ClientMessage;
import commons.Score;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.glassfish.jersey.client.ClientConfig;//import org.springframework.web.bind.annotation.RequestMapping;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Singleplayer leaderboard ctrl.
 */
public class SingleplayerLeaderboardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private int counter = 0;

    @FXML
    Button start;

    /**
     * The Leave.
     */
    @FXML
    Button leave;

    /**
     * The Name field.
     */
    @FXML
    TextField nameField;

    /**
     * The Leaderboard.
     */
    @FXML
    ListView<String> leaderboard;

    /**
     * Instantiates a new Singleplayer leaderboard ctrl.
     *
     * @param server   the server
     * @param mainCtrl the main ctrl
     */
    @Inject
    public SingleplayerLeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Will load the leaderboard.
     */
    public void initialize() {
        start.fire(); //this loads the leaderboard, it is currently bound to the start button
    }

    /**
     * Start a singleplayer game.
     */
    public void start() {
        // 1. Get name from the input and validate
        String playerName = nameField.getText();
        if(playerName == null || playerName.isEmpty()){
            // TODO: Error message displayed in the UI
            return;
        }
        // 2. Create a message and assign player's name to it
        ClientMessage msg = new ClientMessage(commons.ClientMessage.Type.INIT_SINGLEPLAYER,
                mainCtrl.getClientID(), null);
        msg.playerName = playerName;
        // 3. Send the message to the proper endpoint
        server.send("/app/general", msg);
    }

    /**
     * Insert name.
     */
    public void insertName() {
        String name = nameField.getText();
        System.out.println(name); // You need to press enter when inserting you name.
    }

    /**
     * Sets leaderboard elements to display top scores from server from high to low
     *
     */
    public void insertLeaderboard() { //needs to change to import the database leaderboard

        counter++;
        /*
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity("http://localhost:8080/api/scores/get"+
                        "Top" + counter + "Scores", List.class);*/

        List<Score> response = ClientBuilder.newClient(new ClientConfig())
                .target("http://localhost:8080/").path("api/scores/")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Score>>() {});

        ArrayList<String> topScores = new ArrayList<>();
        if(response != null){
            for(Score score : response){
                topScores.add(score.toString());
            }
        }

        System.out.println(topScores);
        System.out.println(response);
        leaderboard.getItems().setAll(topScores);
    }

    /**
     * Leave.
     */
    public void leave(){
        mainCtrl.showSplash();
    }
}
