package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import java.util.List;

public class AdminPanelCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    ListView<String> activityBox;

    @Inject
    public AdminPanelCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){
        displayActivities();
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getAdminPanel());
    }

    /**
     * Displays all activities in bank in the listview
     */
    public void displayActivities(){
        List<Activity> activities = server.getActivities();
        System.out.println(activities.toString());
        for(Activity element: activities){
            activityBox.getItems().add(convertToDisplay(element));
        }
    }

    public String convertToDisplay(Activity activity){
        return "ID: " + activity.id + " - " + activity.title + " - " + activity.consumptionInWh + " Wh";
    }

    /**
     * Allows editing the currently selected activity
     */
    public void editActivity() {
    }

    /**
     * Allows the user to create a new activity and submit it to the database
     */
    public void createActivity(){

    }
}
