package client.scenes;

import com.google.inject.Inject;
import commons.Activity;
import commons.ClientMessage;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.application.Platform.runLater;

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
    ImageView cutAnswer;

    @FXML
    ImageView doublePoints;

    @FXML
    ImageView splitTime;

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

    @FXML
    GridPane jokerMessages;

    @FXML
    ListView emojiHolder;

    @FXML
    AnchorPane anchorPane;

    @Inject
    public MultiplayerScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        optionToID = new HashMap<>();
        runLater(() -> {
           initilizeEmojis();
        });
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
        lockUI();

        if(timer != null){
            timer.stop();
        }

        ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.chosenActivity = optionToID.get(choice);


        mainCtrl.getServer().send("/app/general", msg);
    }

    public void lockUI(){
        canInteractWithUI = false;
        submit.setDisable(true);
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
    public void sendEmoji(){
        String[] dir = ((ImageView) emojiHolder.getSelectionModel().getSelectedItem()).getImage().getUrl().split("/");
        System.out.println(dir[dir.length - 1]);

        ClientMessage msg = new ClientMessage(ClientMessage.Type.SHOW_EMOJI,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.imgName = dir[dir.length - 1];

        mainCtrl.getServer().send("/app/general", msg);
    }

    public void showEmoji(String imgName, String name){
        File f = new File("client/src/main/resources/client/scenes/emojiimages/" + imgName);
        ImageView imageView1 = new ImageView(f.toURI().toString());
        Text playerName = new Text(name);

        //make new image
        imageView1.setFitWidth(50);
        imageView1.setFitHeight(50);
        imageView1.setY(anchorPane.getHeight()- 80);
        imageView1.setX((anchorPane.getWidth()- 90) * Math.random());

        playerName.setX(imageView1.getX() + 25 - name.chars().count()*3);
        playerName.setTextAlignment(TextAlignment.CENTER);
        playerName.setY(imageView1.getY()+ 65);
        playerName.setFill(Color.WHITE);
        //animation y coords
        List<Node> transitions = new ArrayList<>(Arrays.asList(imageView1,playerName));
        for (int i = 0; i < transitions.size(); i++) {
            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.seconds(1));
            transition.setToY(-200);
            transition.setNode(transitions.get(i));
            transition.setOnFinished(event -> removeImage(transition.getNode()));
            //animation fade
            FadeTransition ft = new FadeTransition();
            ft.setFromValue(1.0);
            ft.setToValue(0);
            ft.setDuration(Duration.seconds(10));
            ft.setNode(transitions.get(i));
            //start transitions
            transition.play();
            ft.play();

//            List<Rectangle> options = List.of();
            List<Node> titles = List.of(title1, title2, title3, description1, description2, description3,
                    image1, image2, image3, option1, option2, option3);

            anchorPane.getChildren().add(transitions.get(i));

            anchorPane.getChildren().removeAll(titles);
            anchorPane.getChildren().addAll(titles);
        }


       // anchorPane.getChildren().add(imageView1);
       // anchorPane.getChildren().add(playerName);
    }

    private void removeImage(Node node) {
        anchorPane.getChildren().remove(node);
    }

    public void cutAnswer(MouseEvent event){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.USE_JOKER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.joker = ClientMessage.Joker.CUT_ANSWER;

        mainCtrl.getServer().send("/app/general", msg);

        cutAnswer.setDisable(true);
        cutAnswer.setStyle("visibility: hidden;");
    }

    // doubles your points for this round
    public void doublePoints(MouseEvent event){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.USE_JOKER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.joker = ClientMessage.Joker.DOUBLE_POINTS;

        mainCtrl.getServer().send("/app/general", msg);

        doublePoints.setDisable(true);
        doublePoints.setStyle("visibility: hidden;");
    }

    // halves time of your opponents
    public void lowerTime(){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.USE_JOKER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.joker = ClientMessage.Joker.SPLIT_TIME;

        mainCtrl.getServer().send("/app/general", msg);

        splitTime.setDisable(true);
        splitTime.setStyle("visibility: hidden;");
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

        if(timer != null) timer.stop();

        timer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timeBar.progressProperty(), fractionLeft)),
                new KeyFrame(Duration.seconds(totalTime * fractionLeft), new KeyValue(timeBar.progressProperty(), 0.0))
        );
        timer.play();

        if(fractionLeft < 1.0){
            // show red pulse
            timeBar.setStyle("-fx-accent: #e0503d;");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    timeBar.setStyle("-fx-accent: #21A0E8;");
                }
            }, 100);
        }
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

        for(var rect : optionToID.keySet()) {
            rect.setStyle("-fx-stroke: #fff");
            rect.setDisable(false);
        }
    }

    public void resetUI(){
        canInteractWithUI = true;

        optionToID = new HashMap<>();
        choice = null;
        picked.setStyle("visibility: hidden");
    }

    public void showJokers(){
        cutAnswer.setDisable(false);
        cutAnswer.setStyle("visibility: visible;");
        splitTime.setDisable(false);
        splitTime.setStyle("visibility: visible;");
        doublePoints.setDisable(false);
        doublePoints.setStyle("visibility: visible;");
    }

    public void lockAnswer(MouseEvent mouseEvent) {
        if(!canInteractWithUI) return;

        for(var rect : optionToID.keySet()) rect.setStyle("-fx-border-color: white");

        Rectangle rectangle = (Rectangle) mouseEvent.getSource();

        if(!optionToID.containsKey(rectangle)) return;

        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(false);
        submit.setCursor(Cursor.HAND);

        choice = rectangle;

    }

    public void disableAnswer(long optionID){

        Rectangle target = null;

        for(var entry : optionToID.entrySet()){
            if(Objects.equals(entry.getValue(), optionID))
                target = entry.getKey();
        }

        if(target == null) return;

        optionToID.remove(target);
        target.setStyle("-fx-stroke: #e0503d");
        target.setDisable(true);
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

    public void insertJokerNotification(String playerName, ClientMessage.Joker jokerType){
        String jokerNotificationText = playerName + " has done something weird!";
        switch(jokerType){
            case CUT_ANSWER:
                jokerNotificationText = playerName + " has removed one of their incorrect answers!";
                break;
            case SPLIT_TIME:
                jokerNotificationText = playerName + " has split all of their opponents time!";
                break;
            case DOUBLE_POINTS:
                jokerNotificationText = playerName + " has doubled their points!";
                break;
            default:
                // bad joker type
        }

        // use it to avoid height problems
        RowConstraints con = new RowConstraints();
        con.setPrefHeight(30);
        jokerMessages.getRowConstraints().add(con);

        // construct text
        var textTest = new Text(jokerNotificationText);
        textTest.getStyleClass().add("joker-notification");
        textTest.maxWidth(jokerMessages.getMaxWidth());

        jokerMessages.addRow(jokerMessages.getRowCount(), textTest);
        GridPane.setHalignment(textTest, HPos.RIGHT);

        // remove notification after 3 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runLater(() -> {
                    jokerMessages.getChildren().remove(textTest);
                });
            }
        }, 3000);
    }

    public void initilizeEmojis(){
        File folder = new File("client/src/main/resources/client/scenes/emojiimages");
        File[] listOfFiles = folder.listFiles();
        for (File f: listOfFiles) {
            if (f.isFile()) {
                ImageView img = new ImageView(f.toURI().toString());
                img.setFitHeight(80);
                img.setFitWidth(90);
                emojiHolder.getItems().add(img);
            }
        }
    }

}
