package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.ClientMessage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;


public class SingleplayerScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String choice;
    private Thread timerThread;
    private double timerProgress;
    private HashMap<String, Long> optionToID;

    @FXML
    ProgressBar timeBar;

    @FXML
    Rectangle option1;

    @FXML
    Rectangle option2;

    @FXML
    Rectangle option3;

    @FXML
    ImageView image1;

    @FXML
    ImageView image2;

    @FXML
    ImageView image3;

    @FXML
    Text description1;

    @FXML
    Text description2;

    @FXML
    Text description3;

    @FXML
    Label title1;

    @FXML
    Label title2;

    @FXML
    Label title3;

    @FXML
    Button submit;

    @FXML
    Label score;

    @Inject
    public SingleplayerScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

        optionToID = new HashMap<>();
    }

    public void leave(){
        mainCtrl.showOverview();
    }


    public void lockAnswer(MouseEvent mouseEvent) {
        option1.setStyle("-fx-border-color: white");
        option2.setStyle("-fx-border-color: white");
        option3.setStyle("-fx-border-color: white");
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(false);
        submit.setCursor(Cursor.HAND);

        choice = rectangle.getId();
    }


    public void displayActivities(List<Activity> activities){
        optionToID = new HashMap<>();

        // for convenience
        List<Rectangle> options = List.of(option1, option2, option3);
        List<Label> titles = List.of(title1, title2, title3);
        List<Text> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            if(a == null) continue;

            optionToID.put(options.get(i).getId(), a.id);

            titles.get(i).setText(Integer.toString(a.consumptionInWh));
            descriptions.get(i).setText(a.title);

            images.get(i).setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
        }
    }

    /**
     * Sets visible timer to a desired value and starts decreasing it in the rate calculated using totalTime.
     *
     * @param fractionLeft fraction of a full timer that we should start counting down from
     * @param totalTime time that the full timer corresponds to
     */
    public void setTimer(double fractionLeft, double totalTime){
        timerProgress = fractionLeft;

        // by default, our timer is 10.0s long
        if(totalTime <= 0.0) totalTime = 10.0;
        final double decreaseBy = 0.001 * 10.0 / totalTime;

        if(timerThread != null) timerThread.interrupt();
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (timerProgress>=0.00) {
                    Platform.runLater(() -> {
                        timeBar.setProgress(timerProgress);
                        timerProgress -= decreaseBy;
                        if(timerProgress <= 0){
                            timeBar.setProgress(0);
                        }
                    });
                    Thread.sleep(10);
                }

                return null;
            }
        };

        timerThread = new Thread(task);
        timerThread.start();
    }

    public void submitAnswer(){
        timerThread.interrupt();
        double time = timerProgress;

        ClientMessage msg = new ClientMessage(commons.ClientMessage.Type.SUBMIT_SINGLEPLAYER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.time = time;
        msg.chosenActivity = optionToID.get(choice);
        server.send("/app/general", msg);
    }

    public void setScoreTo(int s){
        score.setText("Score " + s);
    }

    public void stopThreads(){
        timerThread.interrupt();
    }

}
