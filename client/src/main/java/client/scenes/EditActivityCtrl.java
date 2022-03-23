package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

public class EditActivityCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public EditActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void fillActivity(Activity activity){
        System.out.println(activity);
    }

    public void saveActivity(MouseEvent actionEvent) {
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getEditActivity());
    }
}
