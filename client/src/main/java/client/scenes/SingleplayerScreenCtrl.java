package client.scenes;

import com.google.inject.Inject;
import commons.Activity;
import commons.ClientMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SingleplayerScreenCtrl {

    private final MainCtrl mainCtrl;
    private Rectangle choice;
    private HashMap<Rectangle, Long> optionToID;
    private boolean canInteractWithUI;

    private Timeline timer;

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
    Label description1;

    @FXML
    Label description2;

    @FXML
    Label description3;

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

    @FXML
    Label screenTitle;

    @FXML
    Text picked;

    @FXML
    Text result;

    @Inject
    public SingleplayerScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;

        optionToID = new HashMap<>();
    }

    public void leave(){
        // inform the server about leaving
        ClientMessage msg = new ClientMessage(ClientMessage.Type.QUIT,
                mainCtrl.getClientID(), mainCtrl.getGameID());

        mainCtrl.showLeave(mainCtrl::showSingleLeaderboardScreen, () -> mainCtrl.getServer().send("/app/general", msg));
    }

    public void lockAnswer(MouseEvent mouseEvent) {
        if(!canInteractWithUI) return;

        option1.setStyle("-fx-stroke: white");
        option2.setStyle("-fx-stroke: white");
        option3.setStyle("-fx-stroke: white");
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(false);
        submit.setCursor(Cursor.HAND);

        choice = rectangle;
    }


    public void displayActivities(List<Activity> activities){
        optionToID = new HashMap<>();

        // for convenience
        List<Rectangle> options = List.of(option1, option2, option3);
        List<Label> titles = List.of(title1, title2, title3);
        List<Label> descriptions = List.of(description1, description2, description3);
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

    /**
     * Sets visible timer to a desired value and starts decreasing it in the rate calculated using totalTime.
     *
     * @param fractionLeft fraction of a full timer that we should start counting down from
     * @param totalTime time that the full timer corresponds to
     */
    public void setTimer(double fractionLeft, double totalTime){
        // by default, our timer is 10.0s long
        if(totalTime <= 0.0) totalTime = 10.0;

        timer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timeBar.progressProperty(), fractionLeft)),
                new KeyFrame(Duration.seconds(totalTime), new KeyValue(timeBar.progressProperty(), 0.0))
        );
        timer.play();
    }

    public void submitAnswer(){
        if(!canInteractWithUI || choice == null) return;
        lockUI();

        if(timer != null){
            timer.stop();
        }

        ClientMessage msg = new ClientMessage(commons.ClientMessage.Type.SUBMIT_SINGLEPLAYER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.chosenActivity = optionToID.get(choice);
        mainCtrl.getServer().send("/app/general", msg);
    }

    public void lockUI(){
        canInteractWithUI = false;
        submit.setDisable(true);
    }

    public void showAnswer(Long correctID, Long pickedID){
        timeBar.setProgress(0.0);
        for(var entry : optionToID.entrySet()){
            Long activityID = entry.getValue();
            Rectangle op = entry.getKey();

            // set rectangle color
            if(Objects.equals(activityID, correctID)){
                op.setStyle("-fx-stroke: #38c768");
            }
            else{
                op.setStyle("-fx-stroke: #e0503d");
            }

            if(Objects.equals(activityID, pickedID)){
                // render the "You picked this one" text
                picked.setLayoutX(op.getLayoutX() + (op.getWidth() - picked.getLayoutBounds().getWidth()) / 2.0);
                picked.setLayoutY(op.getLayoutY() - 15.0);
                picked.setStyle("visibility: visible");
            }

            if(Objects.equals(correctID, pickedID)){
                result.setText("You got it right :)");
                result.setStyle("visibility: visible");
                timeBar.setStyle("-fx-border-color: #38c768");
            }else{
                result.setText("You got it wrong :(");
                result.setStyle("visibility: visible");
                timeBar.setStyle("-fx-border-color: #e0503d");
            }
        }
    }
    public void setScoreTo(int s){
        score.setText("Score " + s);
    }

    public void setTitleTo(String title){
        screenTitle.setText(title);
    }

    public void restoreView(){
        canInteractWithUI = true;
        picked.setStyle("visibility: hidden");

        option1.setStyle("-fx-stroke: #fff");
        option2.setStyle("-fx-stroke: #fff");
        option3.setStyle("-fx-stroke: #fff");

        result.setStyle("visibility: hidden");
        choice = null;
    }
}
