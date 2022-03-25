package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class EditActivityCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private long currentActivityID;

    @FXML
    TextField titleField;

    @FXML
    TextField consumptionField;

    @FXML
    TextField sourceField;

    @FXML
    TextField imageField;

    @FXML
    Label titleErrorText;

    @FXML
    Label consumptionErrorText;

    @FXML
    Label sourceErrorText;

    @FXML
    Label imageErrorText;


    @Inject
    public EditActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){
        resetErrorText();
    }

    public void fillActivity(Activity activity){
        currentActivityID = activity.id;
        titleField.setText(activity.title);
        consumptionField.setText(activity.consumptionInWh.toString());
        sourceField.setText(activity.source);
        imageField.setText(activity.imagePath);
    }

    public void saveActivity(MouseEvent actionEvent) throws MalformedURLException {
        String title = titleField.getText();
        int consumption;
        String source = sourceField.getText();
        String image = imageField.getText();

        if(title == null || title.length() == 0){
            titleErrorText.setVisible(true);
        }else{
            titleErrorText.setVisible(false);
        }
        if(source == null || source.length() == 0){
            sourceErrorText.setVisible(true);
        }else{
            sourceErrorText.setVisible(false);
        }
        if(image == null || image.length() == 0){
            imageErrorText.setVisible(true);
        }else{
            imageErrorText.setVisible(false);
        }

        try {
            consumption = Integer.parseInt(consumptionField.getText());
            if(consumption > 0){
                consumptionErrorText.setVisible(false);
            }else{
                consumptionErrorText.setVisible(true);
            }
        } catch (final NumberFormatException e) {
            consumptionErrorText.setVisible(true);
            throw new IllegalArgumentException("Invalid number");
        }

        try {
            URL sourceUrl = new URL(source);
            URLConnection srcConn = sourceUrl.openConnection();
            srcConn.connect();
        } catch (MalformedURLException e) {
            sourceErrorText.setVisible(true);
            throw new MalformedURLException("The URL provided is not valid");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(title == null || title.length() == 0
                || consumption <= 0
                || source == null || source.length() == 0
                || image == null  || image.length() == 0){
            throw new IllegalArgumentException("The data provided is invalid");
        }

        Activity newActivity = new Activity(title, consumption,
                source, image);

        server.editActivity(currentActivityID, newActivity);
        mainCtrl.showAdminPanel();
    }

    public void remove(ActionEvent actionEvent){
        server.removeActivity(currentActivityID);
        mainCtrl.showAdminPanel();
    }

    public void resetErrorText(){
        titleErrorText.setVisible(false);
        consumptionErrorText.setVisible(false);
        sourceErrorText.setVisible(false);
        imageErrorText.setVisible(false);
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getEditActivity());
    }
}
