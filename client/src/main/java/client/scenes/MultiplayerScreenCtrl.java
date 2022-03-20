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
import java.util.Objects;


public class MultiplayerScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Rectangle choice;
    private Thread timerThread;
    private double timerProgress;
    private HashMap<Rectangle, Long> optionToID;

    @FXML
    ProgressBar timeBar;

    @FXML
    Rectangle option1;

    @FXML
    Rectangle option2;

    @FXML
    Rectangle option3;

    @FXML
    Button submit;

    @FXML
    ImageView image1;

    @FXML
    ImageView image2;

    @FXML
    ImageView image3;

    @FXML
    Label title1;

    @FXML
    Label title2;

    @FXML
    Label title3;

    @FXML
    Text description1;

    @FXML
    Text description2;

    @FXML
    Text description3;

    @FXML
    Label score;

    @FXML
    Text picked;


    boolean submitted = false;

    @Inject
    public MultiplayerScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        optionToID = new HashMap<>();

    }

    public void leave(){
        mainCtrl.showOverview();
    }

    //submits answer, stops time,
    public void submitAnswer(){
        timerThread.interrupt();
        double time = timerProgress;

        ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.time = time;
        msg.chosenActivity = optionToID.get(choice);
        submit.setDisable(true);
        submitted = true;
        server.send("/app/general", msg);
        System.out.println("Answer submitted");

    }

    public void showAnswer(Long correctID, Long pickedID) {
        for (var entry : optionToID.entrySet()) {
            Long activityID = entry.getValue();
            Rectangle op = entry.getKey();

            // set rectangle color
            if (Objects.equals(activityID, correctID)) {
                op.setStyle("-fx-stroke: #38c768");
            } else {
                op.setStyle("-fx-stroke: #e0503d");
            }

            if (Objects.equals(activityID, pickedID)) {
                // render the "You picked this one" text
                picked.setLayoutX(op.getLayoutX() + (op.getWidth() - picked.getLayoutBounds().getWidth()) / 2.0);
                picked.setLayoutY(op.getLayoutY() - 15.0);
                picked.setStyle("visibility: visible");
            }
        }
    }

    //shows an emoji
    public void showEmoji(MouseEvent event){
        System.out.println(event.getSource());
    }

    //removes oneanswer
    public void cutAnswer(MouseEvent event){
        System.out.println(event.getSource());
    }

    //doubles your points for this round
    public void doublePoints(MouseEvent event){
        System.out.println(event.getSource());
    }

    public void lowerTime(){}
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

    public void displayActivities(List<Activity> activities){
        // for convenience

       resetUI();

        List<Rectangle> options = List.of(option1, option2, option3);
        List<Label> titles = List.of(title1, title2, title3);
        List<Text> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            if(a == null) continue;
            optionToID.put(options.get(i), a.id);
            
            titles.get(i).setText(Integer.toString(a.consumptionInWh));
            descriptions.get(i).setText(a.title);

            images.get(i).setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
        }
    }

    public void resetUI(){
        submitted = false;
        option1.setStyle("-fx-border-color: white");
        option2.setStyle("-fx-border-color: white");
        option3.setStyle("-fx-border-color: white");
        optionToID = new HashMap<>();

    }

    public void lockAnswer(MouseEvent mouseEvent) {
        if(submitted)
            return;

        option1.setStyle("-fx-border-color: white");
        option2.setStyle("-fx-border-color: white");
        option3.setStyle("-fx-border-color: white");
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(false);
        submit.setCursor(Cursor.HAND);

        choice = rectangle;

    }

    public void updateScore(int score){
        this.score.setText("Score: " + score);

    }


}
