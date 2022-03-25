package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CreateActivityCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

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
    public CreateActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){
        resetErrorText();
    }

    public void resetErrorText(){
        titleErrorText.setVisible(false);
        consumptionErrorText.setVisible(false);
        sourceErrorText.setVisible(false);
        imageErrorText.setVisible(false);
    }

    public void clearFields(){
        titleField.clear();
        consumptionField.clear();
        sourceField.clear();
        imageField.clear();
    }

    public void saveActivity() throws MalformedURLException {
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

        Activity newActivity = new Activity(title, consumption, source, image);
        server.addActivity(newActivity);
        mainCtrl.showAdminPanel();
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getCreateActivity());
    }

}
