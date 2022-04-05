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
    Rectangle option1, option2, option3;

    @FXML
    Button submit;

    @FXML
    ImageView image, image1, image2, image3;

    @FXML
    Label title1, title2, title3, title;

    @FXML
    Label description1, description2, description3, description4;

    @FXML
    Rectangle activity;

    @FXML
    Label score;

    @FXML
    Text picked;

    @FXML
    Label headTitle, headTitle1, headTitle2;

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






        Scene scene = mainCtrl.getPrimaryStage().getScene();
        if (mainCtrl.getMultiplayer().equals(scene)) {
            ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                    mainCtrl.getClientID(), mainCtrl.getGameID());
            msg.chosenActivity = optionToID.get(choice);
            mainCtrl.getServer().send("/app/general", msg);
        } else if (mainCtrl.getGuessQuestionM().equals(scene)) {
            ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                    mainCtrl.getClientID(), mainCtrl.getGameID());
            msg.chosenActivity = optionToID.get(choice);
            mainCtrl.getServer().send("/app/general", msg);
        } else if (mainCtrl.getInputQuestionM().equals(scene)) {
            if(textField.getText().equals("")){
                textField.setText("give a Number");
                return;
            }
            try{
                //todo: make a working message
                long answer = Long.parseLong(textField.getText());
                ClientMessage msg = new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER,
                        mainCtrl.getClientID(), mainCtrl.getGameID());
                msg.chosenActivity = answer;
                mainCtrl.getServer().send("/app/general", msg);
            }catch (NumberFormatException e){
                textField.setText("give a Number");
                return;
            }
        }
        canInteractWithUI = false;
        timer.stop();

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

    public void displayActivities(Question question, Scene scene){
        // for convenience



        if (mainCtrl.getMultiplayer().equals(scene)) {
            displayCompareActivities(question.activities);
        } else if (mainCtrl.getGuessQuestionM().equals(scene)) {
            displayGuessActivities(question);
        } else if (mainCtrl.getInputQuestionM().equals(scene)) {
            displayInputActivities(question.activities);
        }


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


            title.setText(Long.toString(a.consumptionInWh));
            description2.setText(a.title);

            image.setImage(new Image("http://localhost:8080/activities/" + a.imagePath));
    }

    public void displayInputActivities(List<Activity> activities){
        choice = new Rectangle();
        canInteractWithUI = true;
        submit.setDisable(false);
        description.setText(activities.get(0).title);

    }

    public void displayCompareActivities(List<Activity> activities){
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
        headTitle1.setText(s[0]);
        if(s.length > 1)
            headTitle2.setText(s[1]);
    }
}
