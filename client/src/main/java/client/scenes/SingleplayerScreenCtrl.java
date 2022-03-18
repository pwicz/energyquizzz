package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
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


public class SingleplayerScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

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

    @Inject
    public SingleplayerScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void leave(){
        mainCtrl.showOverview();
    }

    public void submitAnswer(){

    }


    public void lockAnswer(MouseEvent mouseEvent) {

        option1.setStyle("-fx-border-color: white");
        option2.setStyle("-fx-border-color: white");
        option3.setStyle("-fx-border-color: white");
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        rectangle.setStyle("-fx-stroke: linear-gradient(#38c768, #21A0E8)");
        submit.setDisable(false);
        submit.setCursor(Cursor.HAND);
    }

    public void displayActivities(List<Activity> activities){
        // for convenience
        List<Label> titles = List.of(title1, title2, title3);
        List<Text> descriptions = List.of(description1, description2, description3);
        List<ImageView> images = List.of(image1, image2, image3);

        for(int i = 0; i < activities.size() && i < 3; ++i){
            Activity a = activities.get(i);

            titles.get(i).setText(Integer.toString(a.consumptionInWh));
            descriptions.get(i).setText(a.title);

            File file = new File(a.imagePath);
            images.get(i).setImage(new Image(file.toURI().toString()));
        }
    }

}
