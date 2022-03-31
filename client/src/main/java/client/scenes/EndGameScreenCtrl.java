package client.scenes;

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
    Label yourScore;

    @FXML
    Label score;

    @Inject
    public EndGameScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void insertLeaderboard(List<String> players)  { //needs to change to import the database leaderboard
        leaderboardSingle.getItems().clear();
        leaderboardSingle.getItems().addAll(players);
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).contains(mainCtrl.getName())){
                //set name
                yourScore.setText("#" + (i+1) + " " + mainCtrl.getName());
                //set score
                score.setText("Score: " + players.get(i).replace(mainCtrl.getName()+":", ""));
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
