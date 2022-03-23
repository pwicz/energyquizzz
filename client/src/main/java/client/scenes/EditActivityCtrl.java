package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;

import javax.inject.Inject;

public class EditActivityCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public EditActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(){

    }

    public void fillActivity(Activity activity){

    }
}
