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

    @Inject
    public InBetweenScoresCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void updateQuestionCounter(int number) {
        questionCounter.setText(number + "/20");
    }
}
