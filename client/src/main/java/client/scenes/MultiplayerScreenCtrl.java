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


import java.io.File;
import java.util.List;


public class MultiplayerScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String choice;

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



    Thread timerThread;
    double timerProgress;

    @Inject
    public MultiplayerScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void leave(){
        mainCtrl.showOverview();
    }

    //submits answer, stops time,
    public void submitAnswer(){
        server.send("/app/general", new ClientMessage(ClientMessage.Type.SUBMIT_ANSWER, mainCtrl.getClientID(), "0"));
        System.out.println("Answer submitted");

    }
    public void showAnswer(){
        System.out.println("Answer showed");
        switch (choice){
            case "option1":
                option1.setStyle("-fx-border-color: #38c768");
                break;
            case "option2":
                option2.setStyle("-fx-border-color: #38c768");
                break;
            case "option3":
                option3.setStyle("-fx-border-color: #38c768");
                break;
            default:
                System.out.println("Invalid answer");
                break;
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
        activities = server.getActivites();
        System.out.println(activities);
        List<Label> titles = List.of(title1, title2, title3);
        List<Text> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            if(a == null) continue;
            
            titles.get(i).setText(Integer.toString(a.consumptionInWh));
            descriptions.get(i).setText(a.title);

            File file = new File("server/src/main/resources/public/activities/" + a.imagePath);
            images.get(i).setImage(new Image(file.toURI().toString()));
        }
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

        System.out.println(rectangle.getId());
        System.out.println(choice);
    }


}
