package client.scenes;

import commons.Score;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import java.util.List;

public class EndGameScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    Button leave;

    @FXML
    ListView leaderboardSingle;

    @FXML
    Label yourPlace;

    @FXML
    Label yourScore;

    @Inject
    public EndGameScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void insertLeaderboard(List<Score> players)  { //needs to change to import the database leaderboard
        leaderboardSingle.getItems().clear();
        leaderboardSingle.setCellFactory(lv -> new CustomListCell());
        leaderboardSingle.getItems().addAll(players);
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).getPlayerName().equals(mainCtrl.getName())){
                //set name
                yourPlace.setText("#" + (i+1) + " " + mainCtrl.getName());
                //set score
                yourScore.setText("Your Score: " + players.get(i).getPlayerScore());
            }
        }
    }

    public void leave(){
        mainCtrl.showSplash();
    }

    public void showWaitingRoom(){
        mainCtrl.showinputNameScreen();
    }

}
