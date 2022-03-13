package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javax.inject.Inject;

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
    ListView leaderboard;



    @Inject
    public SingleplayerLeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void start() { //start a singleplayer game
    }

    public void insertName() {
        String name =nameField.getText();
        System.out.println(name); // You need to press enter when inserting you name.
    }

    public void leave(){
        mainCtrl.showSplash();
    }
}
