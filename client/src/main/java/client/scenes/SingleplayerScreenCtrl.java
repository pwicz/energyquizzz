package client.scenes;

import com.google.inject.Inject;
import commons.Activity;
import commons.ClientMessage;
import commons.Question;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.DecimalFormat;
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
    Rectangle option1, option2, option3, activity;

    @FXML
    ImageView image1, image2, image3, image;

    @FXML
    Label description4,description1, description2, description3;

    @FXML
    Label title, title1, title2, title3;

    @FXML
    Button submit;

    @FXML
    Label score;

    @FXML
    Label screenTitle, screenTitle1, screenTitle2;

    @FXML
    Text picked, result, answerInput;

    @FXML
    Text description;

    @FXML
    TextField textField;


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


    public void displayActivities(Question question, Scene scene){


        if (mainCtrl.getSingleplayerScreen().equals(scene)) {
            displayCompareActivities(question);
        } else if (mainCtrl.getSingleplayerGuessScreen().equals(scene)) {
            displayGuessActivities(question);
        } else if (mainCtrl.getSingleplayerInputScreen().equals(scene)) {
            displayInputActivities(question.activities);
        }
        timeBar.setStyle("-fx-border-color: #38c768");

    }

    private void displayCompareActivities(Question question) {
        List<Activity> activities = question.activities;
        optionToID = new HashMap<>();

        // for convenience
        List<Rectangle> options = List.of(option1, option2, option3);
        List<Label> titles = List.of(title1, title2, title3);
        List<Label> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            if(a == null) continue;

            optionToID.put(options.get(i), a.consumptionInWh);

            titles.get(i).setText(Long.toString(a.consumptionInWh));
            descriptions.get(i).setText(a.title);

            images.get(i).setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
        }


    }
    private void displayGuessActivities(Question question) {
        optionToID = new HashMap<>();
        List<Label> descriptions = List.of(description1, description2, description3);
        List<Rectangle> options = List.of(option1, option2, option3);
        Activity a = question.getActivities().get(0);
        switch (question.type) {
            case GUESS:
                for (int i = 0; i < descriptions.size(); i++) {
                    descriptions.get(i).setText(question.options.get(i).toString() + " wh");
                    optionToID.put(options.get(i), question.options.get(i));
                }
                break;
            case HOW_MANY_TIMES:
                for (int i = 0; i < descriptions.size(); i++) {
                    descriptions.get(i).setText(question.options.get(i).toString() + " Times");
                    optionToID.put(options.get(i), question.options.get(i));
                }
                break;
            default:
        }
        title.setText(Long.toString(a.consumptionInWh));
        description4.setText(a.title);

        image.setImage(new Image("http://localhost:8080/activities/" + a.imagePath));

    }
    private void displayInputActivities(List<Activity> activities) {
        answerInput.setStyle("visibility: hidden");
        result.setStyle("visibility: hidden");
        choice = new Rectangle();
        canInteractWithUI = true;
        submit.setDisable(false);
        description.setText(activities.get(0).title);

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

        Scene scene = mainCtrl.getPrimaryStage().getScene();
        if (mainCtrl.getSingleplayerScreen().equals(scene)) {
            ClientMessage msg = new ClientMessage(commons.ClientMessage.Type.SUBMIT_SINGLEPLAYER,
                    mainCtrl.getClientID(), mainCtrl.getGameID());
            msg.chosenActivity = optionToID.get(choice);
            mainCtrl.getServer().send("/app/general", msg);
        } else if (mainCtrl.getSingleplayerGuessScreen().equals(scene)) {
            ClientMessage msg = new ClientMessage(commons.ClientMessage.Type.SUBMIT_SINGLEPLAYER,
                    mainCtrl.getClientID(), mainCtrl.getGameID());
            msg.chosenActivity = optionToID.get(choice);
            mainCtrl.getServer().send("/app/general", msg);
        } else if (mainCtrl.getSingleplayerInputScreen().equals(scene)) {
            if(textField.getText().equals("")){
                textField.setText("give a Number");
                return;
            }
            try{
                //todo: make a working message
                long answer = Long.parseLong(textField.getText());
                ClientMessage msg = new ClientMessage(commons.ClientMessage.Type.SUBMIT_SINGLEPLAYER,
                        mainCtrl.getClientID(), mainCtrl.getGameID());
                msg.chosenActivity = answer;
                mainCtrl.getServer().send("/app/general", msg);
            }catch (NumberFormatException e){
                textField.setText("give a Number");
                return;
            }
        }

        canInteractWithUI = false;

        if(timer != null){
            timer.stop();
        }

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

    public void showAnswerInput(Long correctID, Long pickedID){
        timeBar.setProgress(0.0);

        DecimalFormat df = new DecimalFormat("#.#");
        double dif = (double)pickedID/correctID;
        String percentage =  df.format(100 * (Math.abs(1 - dif  )));

        if(correctID*0.5 <= pickedID && correctID*1.5 >= pickedID ) {
            answerInput.setStyle("visibility: visible");
            answerInput.setFill(Color.web("#38c768"));
            answerInput.setText("The correct answer was " + correctID + "\nyou were " + percentage + "% off");
            result.setText("You got it right :)");
            result.setStyle("visibility: visible");
            timeBar.setStyle("-fx-border-color: #38c768");
        }
        else {
            answerInput.setStyle("visibility: visible");
            answerInput.setFill(Color.web("#e0503d"));
            answerInput.setStyle("-fx-text-fill: #e0503d");
            answerInput.setText("The correct answer was " + correctID + "\nyou were " + percentage + "% off");
            result.setText("You got it wrong :(");
            result.setStyle("visibility: visible");
            timeBar.setStyle("-fx-border-color: #e0503d");
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


    public void setHeadGuessTitle(String screenTitle) {
        String[] s = screenTitle.split(",");
        screenTitle1.setText(s[0]);
        screenTitle2.setText("");
        screenTitle1.setStyle("-fx-font-size: 40");
        screenTitle2.setStyle("-fx-font-size: 40");
        if(s.length > 1) {
            screenTitle2.setText(s[1]);
            screenTitle1.setStyle("-fx-font-size: 25");
            screenTitle2.setStyle("-fx-font-size: 25");
        }
    }
}
