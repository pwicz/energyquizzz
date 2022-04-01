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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MultiplayerScreenCtrl {

    private final MainCtrl mainCtrl;
    public Text description;
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
    Label description1;

    @FXML
    Label description2;

    @FXML
    Label description3;

    @FXML
    Label score;

    @FXML
    Text picked;

    @FXML
    Label headTitle;

    @Inject
    public MultiplayerScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        optionToID = new HashMap<>();
    }

    public void leave(){
        // inform the server about leaving
        ClientMessage msg = new ClientMessage(ClientMessage.Type.QUIT,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        mainCtrl.showLeave(mainCtrl::showSplash, () -> mainCtrl.getServer().send("/app/general", msg));
    }

    //submits answer, stops time,
    public void submitAnswer(){
        if(!canInteractWithUI || choice == null) return;
        canInteractWithUI = false;

        timer.stop();

        submit.setDisable(true);

        ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.chosenActivity = optionToID.get(choice);


        mainCtrl.getServer().send("/app/general", msg);
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
        // by default, our timer is 10.0s long
        if(totalTime <= 0.0) totalTime = 10.0;

        timer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timeBar.progressProperty(), fractionLeft)),
                new KeyFrame(Duration.seconds(totalTime), new KeyValue(timeBar.progressProperty(), 0.0))
        );
        timer.play();
    }

    public void displayActivities(List<Activity> activities){
        // for convenience

       resetUI();

        List<Rectangle> options = List.of(option1, option2, option3);
        List<Label> titles = List.of(title1, title2, title3);
        List<Label> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            if(a == null) continue;
            optionToID.put(options.get(i), a.id);
            
            titles.get(i).setText(Long.toString(a.consumptionInWh));
            descriptions.get(i).setText(a.title);

            images.get(i).setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
        }
    }

    public void resetUI(){
        canInteractWithUI = true;

        option1.setStyle("-fx-stroke: #fff");
        option2.setStyle("-fx-stroke: #fff");
        option3.setStyle("-fx-stroke: #fff");
        optionToID = new HashMap<>();
        choice = null;
        picked.setStyle("visibility: hidden");

    }

    public void lockAnswer(MouseEvent mouseEvent) {
        if(!canInteractWithUI) return;

        option1.setStyle("-fx-border-color: white");
        option2.setStyle("-fx-border-color: white");
        option3.setStyle("-fx-border-color: white");
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(false);
        submit.setCursor(Cursor.HAND);

        choice = rectangle;

    }

    public void updateTitle(int question){
        headTitle.setText("Question " + (question+1) +": Which activity consumes most energy?");
    }

    public void updateScore(int score){
        this.score.setText("Score: " + score);
    }


    public void enterAnswer(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.L)){ //later should replace L with ENTER
            submitAnswer();
        }
    }
}
