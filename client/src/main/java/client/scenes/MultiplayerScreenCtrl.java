package client.scenes;

import com.google.inject.Inject;
import commons.Activity;
import commons.ClientMessage;
import commons.Question;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
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
import java.text.DecimalFormat;
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

    private List<Timer> notifications;

    private boolean cutAnswerUsed = false;
    private boolean doublePointsUsed = false;
    private boolean splitTimeUsed = false;

    @FXML
    ProgressBar timeBar;

    @FXML
    Rectangle option1, option2, option3;

    @FXML
    Button submit;

    @FXML
    ImageView cutAnswer;

    @FXML
    ImageView doublePoints;

    @FXML
    ImageView splitTime;

    @FXML
    ImageView image, image1, image2, image3;

    @FXML
    Label description1, description2, description3, description4;

    @FXML
    Rectangle activity;

    @FXML
    Label score;

    @FXML
    Text picked, result, answerInput;

    @FXML
    Label headTitle, headTitle1, headTitle2;

    @FXML
    GridPane jokerMessages;

    @FXML
    ListView emojiHolder;

    @FXML
    AnchorPane anchorPane;

    @FXML
    TextField textField;

    @Inject
    public MultiplayerScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        optionToID = new HashMap<>();
        runLater(this::initilizeEmojis);

        notifications = new ArrayList<>();
    }

    public void leave(){
        // inform the server about leaving
        ClientMessage msg = new ClientMessage(ClientMessage.Type.QUIT,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        mainCtrl.showLeave(mainCtrl::showSplash,
                () ->{
            mainCtrl.getServer().send("/app/general", msg);
            timeStop();
        });
    }

    //submits answer, stops time,
    public void submitAnswer(){
        if(!canInteractWithUI || choice == null) return;

        ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                mainCtrl.getClientID(), mainCtrl.getGameID());

        Scene scene = mainCtrl.getPrimaryStage().getScene();
        if (mainCtrl.getInputQuestionM().equals(scene)) {
            if(textField.getText().equals("")){
                return;
            }
            try{
                msg.chosenActivity = Long.parseLong(textField.getText());
                mainCtrl.getServer().send("/app/general", msg);
            } catch (NumberFormatException e){
                textField.setText("incorrect number");
                return;
            }
        }
        else{
            msg.chosenActivity = optionToID.get(choice);
            mainCtrl.getServer().send("/app/general", msg);
        }

        lockUI();

        if(timer != null){
            timer.stop();
        }
    }

    public void lockUI(){
        canInteractWithUI = false;
        submit.setDisable(true);

        if(!doublePointsUsed){
            doublePoints.setStyle("-fx-opacity: 0.2");
            doublePoints.setDisable(true);
        }
        if(!cutAnswerUsed){
            cutAnswer.setStyle("-fx-opacity: 0.2");
            cutAnswer.setDisable(true);
        }
    }

    public void lockSplitTimeJoker(){
        if(!splitTimeUsed){
            splitTime.setStyle("-fx-opacity: 0.2");
            splitTime.setDisable(true);
        }
    }

    public void showAnswer(Long correctID, Long pickedID, int pointReceived) {
        timeBar.setProgress(0.0);
        for(var entry : optionToID.entrySet()){
            Long activityID = entry.getValue();
            Rectangle op = entry.getKey();

            System.out.println("CORRECT ID: " + correctID + ", ActivityID: " + activityID);

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
                result.setText("You got it right :) +" + pointReceived + " points");
                result.setStyle("visibility: visible");
                timeBar.setStyle("-fx-border-color: #38c768");
            }else{
                result.setText("You got it wrong :(");
                result.setStyle("visibility: visible");
                timeBar.setStyle("-fx-border-color: #e0503d");
            }
        }
    }

    public void showAnswerInput(boolean answeredCorrect, Long correctID, Long pickedID, int pointReceived){
        timeBar.setProgress(0.0);

        DecimalFormat df = new DecimalFormat("#.#");
        double dif = (double)pickedID/correctID;
        String percentage = df.format(Math.abs(1.0 - dif) * 100);

        if(answeredCorrect) {
            answerInput.setStyle("visibility: visible");
            answerInput.setFill(Color.web("#38c768"));
            result.setText("You got it right :) +" + pointReceived + " points");
            result.setStyle("visibility: visible");
            timeBar.setStyle("-fx-border-color: #38c768");
        }
        else {
            answerInput.setStyle("visibility: visible");
            answerInput.setFill(Color.web("#e0503d"));
            result.setText("You got it wrong :(");
            result.setStyle("visibility: visible");
            timeBar.setStyle("-fx-border-color: #e0503d");
        }

        answerInput.setText("The correct answer was " + correctID + "\nyou were " + percentage + "% off");
    }

    //shows an emoji
    public void sendEmoji(){
        String[] dir = ((ImageView) emojiHolder.getSelectionModel().getSelectedItem()).getImage().getUrl().split("/");

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
//            List<Node> titles = List.of(title1, title2, title3, description1, description2, description3,
//                    image1, image2, image3, option1, option2, option3);

            anchorPane.getChildren().add(transitions.get(i));

//            anchorPane.getChildren().removeAll(titles);
//            anchorPane.getChildren().addAll(titles);
        }

    }

    private void removeImage(Node node) {
        anchorPane.getChildren().remove(node);
    }

    public void cutAnswer(MouseEvent event){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.USE_JOKER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.joker = ClientMessage.Joker.CUT_ANSWER;

        mainCtrl.getServer().send("/app/general", msg);
        mainCtrl.hideCutAnswerJokers();
    }

    public void useCutAnswer(){
        cutAnswer.setDisable(true);
        cutAnswer.setStyle("visibility: hidden;");
        cutAnswerUsed = true;
    }


    // doubles your points for this round
    public void doublePoints(MouseEvent event){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.USE_JOKER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.joker = ClientMessage.Joker.DOUBLE_POINTS;

        mainCtrl.getServer().send("/app/general", msg);
        mainCtrl.hideDoublePointsJoker();
    }

    public void useDoublePoints(){
        doublePoints.setDisable(true);
        doublePoints.setStyle("visibility: hidden;");
        doublePointsUsed = true;
    }

    // halves time of your opponents
    public void lowerTime(){
        ClientMessage msg = new ClientMessage(ClientMessage.Type.USE_JOKER,
                mainCtrl.getClientID(), mainCtrl.getGameID());
        msg.joker = ClientMessage.Joker.SPLIT_TIME;

        mainCtrl.getServer().send("/app/general", msg);
        mainCtrl.hideLowerTimeJoker();
    }

    public void useLowerTime(){
        splitTime.setDisable(true);
        splitTime.setStyle("visibility: hidden;");
        splitTimeUsed = true;
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

    public void displayActivities(Question question, Scene scene){
        // for convenience
        if (mainCtrl.getMultiplayer().equals(scene)) {
            displayCompareActivities(question.activities);
        } else if (mainCtrl.getGuessQuestionM().equals(scene)) {
            displayGuessActivities(question);
        } else if (mainCtrl.getInputQuestionM().equals(scene)) {
            displayInputActivities(question.activities);
        }
        timeBar.setStyle("-fx-border-color: #38c768");

    }

    public void displayGuessActivities(Question question){
        resetUI();
        Activity a = question.getActivities().get(0);
        optionToID = new HashMap<>();
        List<Label> descriptions = List.of(description1, description3 , description4);
        List<Rectangle> options = List.of(option1, option2, option3);
        switch (question.type){
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

        description2.setText(a.title);

        a.imagePath = a.imagePath.replace(" ", "%20");
        image.setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
    }

    public void displayInputActivities(List<Activity> activities){
        resetJokers();
        textField.setText("");

        answerInput.setStyle("visibility: hidden");
        result.setStyle("visibility: hidden");
        choice = new Rectangle();
        canInteractWithUI = true;
        submit.setDisable(false);
        description.setText(activities.get(0).title);
    }

    public void displayCompareActivities(List<Activity> activities){
        resetUI();
        List<Rectangle> options = List.of(option1, option2, option3);
        List<Label> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            if(a == null) continue;
            optionToID.put(options.get(i), a.consumptionInWh);

            descriptions.get(i).setText(a.title);

            a.imagePath = a.imagePath.replace(" ", "%20");
            images.get(i).setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
        }

        for(var rect : optionToID.keySet()) {
            rect.setStyle("-fx-stroke: #fff");
            rect.setDisable(false);
        }
    }

    public void resetUI(){
        canInteractWithUI = true;

        result.setStyle("visibility: hidden");
        option1.setStyle("-fx-stroke: #fff");
        option2.setStyle("-fx-stroke: #fff");
        option3.setStyle("-fx-stroke: #fff");
        option1.setDisable(false);
        option2.setDisable(false);
        option3.setDisable(false);

        optionToID = new HashMap<>();
        choice = null;
        picked.setStyle("visibility: hidden");

        resetJokers();
    }

    public void resetJokers(){
        if(!cutAnswerUsed) {
            cutAnswer.setDisable(false);
            cutAnswer.setStyle("-fx-opacity: 1.0");
        }
        if(!doublePointsUsed){
            doublePoints.setDisable(false);
            doublePoints.setStyle("-fx-opacity: 1.0");
        }
        if(!splitTimeUsed){
            splitTime.setDisable(false);
            splitTime.setStyle("-fx-opacity: 1.0");
        }
    }

    public void showJokers(){
        cutAnswer.setDisable(false);
        cutAnswer.setStyle("visibility: visible;");
        cutAnswerUsed = false;

        splitTime.setDisable(false);
        splitTime.setStyle("visibility: visible;");
        splitTimeUsed = false;

        doublePoints.setDisable(false);
        doublePoints.setStyle("visibility: visible;");
        doublePointsUsed = false;
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

//         remove notification after 3 seconds
        var x = new Timer();
        x.schedule(new TimerTask() {
            @Override
            public void run() {
                runLater(() -> {
                    System.out.println("REMOVING");
                    if(jokerMessages != null){
                        jokerMessages.getChildren().remove(textTest);
                    }
                });
            }
        }, 3000);
        notifications.add(x);
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

    public void setHeadTitle(String headTitle) {
        this.headTitle.setText(headTitle);
    }

    public void setHeadGuessTitle(String headTitle) {
        String[] s = headTitle.split(",");
        headTitle1.setStyle("-fx-font-size: 40 ");
        headTitle2.setStyle("-fx-font-size: 40 ");
        headTitle2.setText("");
        headTitle1.setText(s[0]);
        if(s.length > 1) {
            headTitle2.setText(s[1]);
            headTitle1.setStyle("-fx-font-size: 25 ");
            headTitle2.setStyle("-fx-font-size: 25 ");

        }
    }

    public void checkInput(){
        String text = textField.getText();
        if(text.length() < 1) return;

        int caret = textField.getCaretPosition();

        StringBuilder validatedText = new StringBuilder();

        for(int i = 0; i < text.length(); ++i){
            int charValue = text.charAt(i) - '0';
            if(charValue >= 0 && charValue <= 9){
                validatedText.append(text.charAt(i));
            }
        }

        textField.setText(validatedText.toString());
        textField.positionCaret(caret);
    }

    public void timeStop(){
        if(timer!= null)
            timer.stop();
    }

    public void cleanup(){
        for(var t : notifications) t.cancel();
    }
}
