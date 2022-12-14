package client.scenes;

import commons.ClientMessage;
import commons.Score;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import java.util.List;

/**
 * The type Singleplayer leaderboard ctrl.
 */
public class SingleplayerLeaderboardCtrl {

    private final MainCtrl mainCtrl;

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
    ListView<Score> leaderboard;

    /**
     * Instantiates a new Singleplayer leaderboard ctrl.
     *
     * @param mainCtrl the main ctrl
     */
    @Inject
    public SingleplayerLeaderboardCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
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
        mainCtrl.getServer().send("/app/general", msg);
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
        leaderboard.getItems().clear();
        List<Score> response = mainCtrl.getServer().getTopScores();
        leaderboard.setCellFactory(lv -> new CustomListCell());
        if(response != null){
            for(Score score : response){
                leaderboard.getItems().add(score);
            }
        }
    }


    /**
     * Leave.
     */
    public void leave(){
        mainCtrl.showSplash();
    }
}
