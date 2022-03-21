package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;

public class InBetweenScoreCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    Button leave;

    @FXML
    ListView<String> leaderboard;

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

    public void insertLeaderboard() { //needs to change to import the database leaderboard
        leaderboard.getItems().addAll("Justin", "Piotr", "Mike", "Ioana", "Alex");
    }
    public void insertLeaderboardG() { //needs to change to import the database leaderboard
        leaderboardG.getItems().addAll("Random", "Names", "Here");
    }
    public void insertLeaderboardR() { //needs to change to import the database leaderboard
        leaderboardR.getItems().addAll("Blah", "Blah", "Blah");
    }

    public void setQuestionNo(int n){
        if(n != 20)
            questionNo.setText(n + "/20");
        else
            questionNo.setText("20/20\nEnd of the game");
    }

    public void setScoreTo(int s){
        score.setText("Score: " + s);
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getInBetweenScore());
    }

}
