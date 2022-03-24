package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javafx.scene.control.TextField;

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


    @Inject
    public EditActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void fillActivity(Activity activity){
        currentActivityID = activity.id;
        titleField.setText(activity.title);
        consumptionField.setText(activity.consumptionInWh.toString());
        sourceField.setText(activity.source);
        imageField.setText(activity.imagePath);
    }

    public void saveActivity(MouseEvent actionEvent) {
        Activity newActivity = new Activity(titleField.getText(), Integer.parseInt(consumptionField.getText()),
                sourceField.getText(), imageField.getText());

        //server.editActivity(currentActivityID, newActivity);
    }

    public void remove(ActionEvent actionEvent){
        //server.removeActivity(currentActivityID);
        mainCtrl.showAdminPanel();
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getEditActivity());
    }
}
