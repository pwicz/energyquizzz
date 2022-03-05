package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class MultiplayerScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    AnchorPane anchorPane;

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


    double progress;
    Thread t;

    @Inject
    public MultiplayerScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void leave(){
        mainCtrl.showOverview();
    }

    public void decreaseTime(){
        if(t != null)
            t.interrupt();
        progress = 1.00;
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (progress>=0.00) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            timeBar.setProgress(progress);
                            progress -= 0.001;
                            if(progress <= 0){
                                timeBar.setProgress(0);
                            }
                        }
                    });
                    Thread.sleep(10);
                }

                return null;
            }
        };

        t = new Thread(task);
        t.start();
    }

    public void lockAnswer(MouseEvent mouseEvent) {
        option1.setStroke(Color.WHITE);
        option2.setStroke(Color.WHITE);
        option3.setStroke(Color.WHITE);
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(true);
    }

    public void showEmoji(MouseEvent mouseEvent){
        double width = anchorPane.getWidth()- 90;
        double height = anchorPane.getHeight()- 80;
        System.out.println(anchorPane.getHeight());
        ImageView imageView = (ImageView) mouseEvent.getSource();
        ImageView imageView1 = new ImageView(imageView.getImage());
        imageView1.setFitWidth(imageView.getFitWidth());
        imageView1.setFitHeight(imageView.getFitHeight());
        imageView1.setY(height);
        imageView1.setX(width * Math.random());
//
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(3));
        transition.setToY(-200);
        transition.setNode(imageView1);
        transition.play();
        anchorPane.getChildren().add(imageView1);
        System.out.println("clicked");

    }
}
