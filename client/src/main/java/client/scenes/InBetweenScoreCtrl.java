package client.scenes;

import client.utils.ServerUtils;
import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import java.util.List;

public class InBetweenScoreCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    Button leave;

    @FXML
    ListView<String> leaderboardSingle;

    @FXML
    ListView<String> leaderboardG;

    @FXML
    ListView<String> leaderboardR;

    @FXML
    Label questionNo;

    @FXML
    Label score;

    @Inject
    public InBetweenScoreCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void initialize() {

    }

    public void insertLeaderboard(List<String> players)  { //needs to change to import the database leaderboard
        leaderboardSingle.getItems().clear();
        leaderboardSingle.getItems().addAll(players);

    }
    public void insertLeaderboardG(List<String> players) { //needs to change to import the database leaderboard
        leaderboardG.getItems().clear();
        leaderboardG.getItems().addAll(players);
    }
    public void insertLeaderboardR(List<String> players) { //needs to change to import the database leaderboard
        leaderboardR.getItems().clear();
        leaderboardR.getItems().addAll(players);
    }

    public void setQuestionNo(int n, int total){
        if(n != total)
            questionNo.setText(n + " / " + total);
        else
            questionNo.setText(n + " / " + total + "\nEnd of the game");
    }

    public void setScoreTo(int s){
        score.setText("Score: " + s);
    }

    public void leave(){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.QUIT,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        mainCtrl.showLeave(mainCtrl.getMultiplayer(), () -> server.send("/app/general", msg));
    }

}
