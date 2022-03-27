package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.inject.Inject;
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
        int consumption = 0;
        String source = sourceField.getText();
        String image = imageField.getText();
        boolean canBeSaved = true;

        if(title == null || title.length() == 0){
            canBeSaved = false;
            titleErrorText.setVisible(true);
        }else{
            titleErrorText.setVisible(false);
        }
        if(source == null || source.length() == 0){
            canBeSaved = false;
            sourceErrorText.setVisible(true);
        }else{
            sourceErrorText.setVisible(false);
        }
        if(image == null || image.length() == 0){
            canBeSaved = false;
            imageErrorText.setVisible(true);
        }else{
            imageErrorText.setVisible(false);
        }

        try {
            consumption = Integer.parseInt(consumptionField.getText());
            if(consumption > 0){
                consumptionErrorText.setVisible(false);
            }else{
                canBeSaved = false;
                consumptionErrorText.setVisible(true);
            }
        } catch (final NumberFormatException e) {
            canBeSaved = false;
            consumptionErrorText.setVisible(true);
        }

        try {
            assert source != null;
            URL sourceUrl = new URL(source);
            URLConnection srcConn = sourceUrl.openConnection();
            srcConn.connect();
        } catch (Exception e) {
            canBeSaved = false;
            sourceErrorText.setVisible(true);
        }

        if(canBeSaved){
            Activity newActivity = new Activity(title, consumption, source, image);
            server.addActivity(newActivity);
            mainCtrl.showAdminPanel();
        }
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getCreateActivity());
    }

}
