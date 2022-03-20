package client.scenes;
import com.google.inject.Inject;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InBetweenScoresCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    Label questionCounter;

    @FXML
    Label score;

    @Inject
    public InBetweenScoresCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void updateQuestionCounter(int number) {
        if(number != 20)
              questionCounter.setText(number + "/20");
        else
            questionCounter.setText("20/20\nEnd of the game");
    }

    public void updateScore(int score){
        this.score.setText("Score: " + score);

    }

}
