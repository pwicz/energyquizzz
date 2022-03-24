package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class AdminPanelCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    ListView<String> activityBox;

    @FXML
    Button editActivity;


    Activity selected;
    private HashMap<String, Activity> optionToActivity;

    @Inject
    public AdminPanelCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){
        editActivity.setDisable(true);
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getAdminPanel());
    }

    /**
     * Displays all activities in bank in the listview
     */
    public void displayActivities(){
        optionToActivity = new HashMap<>();
        List<Activity> activities = server.getActivities();

        for(Activity activity:activities){
            optionToActivity.put(convertToDisplay(activity), activity);
            activityBox.getItems().add(convertToDisplay(activity));
        }
    }

    @FXML
    public void setCurrentActivity(MouseEvent mouseEvent){
        String activityText = activityBox.getSelectionModel().getSelectedItem();;
        selected = optionToActivity.get(activityText);
        editActivity.setDisable(false);
        System.out.println(selected);
    }

    public String convertToDisplay(Activity activity){
        return "ID: " + activity.id + " - " + activity.title + " - Consumption: " + activity.consumptionInWh + " Wh";
    }

    /**
     * Allows editing the currently selected activity
     */
    public void editActivity() {
        if(selected != null){
            mainCtrl.showEditActivity(selected);
        }
    }

    /**
     * Allows the user to create a new activity and submit it to the database
     */
    public void createActivity(){
        mainCtrl.showCreateActivity();
    }
}
