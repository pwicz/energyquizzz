package client.scenes;

import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import java.util.List;

public class InBetweenScoreCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    Label endOfGame;

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
    public InBetweenScoreCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void initialize() {
        endOfGame.setVisible(false);
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
        if(n != total){
            questionNo.setText(n + " / " + total);
            endOfGame.setVisible(false);
        } else {
            questionNo.setText(n + " / " + total);
            endOfGame.setVisible(true);
            endOfGame.setText("End of game");
        }
    }

    public void setScoreTo(int s){
        score.setText("Score: " + s);
    }

    public void leave(){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.QUIT,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        mainCtrl.showLeave(mainCtrl::showSplash, () -> mainCtrl.getServer().send("/app/general", msg));
    }

}
