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
    Label errorText;


    @Inject
    public CreateActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){
        resetErrorText();
    }

    public void resetErrorText(){
        errorText.setVisible(false);
    }

    public void clearFields(){
        titleField.clear();
        consumptionField.clear();
        sourceField.clear();
        imageField.clear();
    }

    public void saveActivity() throws MalformedURLException {
        String title = titleField.getText();
        int consumption = Integer.parseInt(consumptionField.getText());
        String source = sourceField.getText();
        String image = imageField.getText();

        if(title == null
                || consumption <= 0
                || source == null
                || image == null)
        {
            throw new IllegalArgumentException("The data provided for the activity is invalid");
        }

        try {
            URL sourceUrl = new URL(source);
            URLConnection srcConn = sourceUrl.openConnection();
            srcConn.connect();
        } catch (MalformedURLException e) {
            errorText.setVisible(true);
            throw new MalformedURLException("The URL provided is not valid");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Activity newActivity = new Activity(title, consumption, source, image);
        server.addActivity(newActivity);
        mainCtrl.showAdminPanel();
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getEditActivity());
    }
}
