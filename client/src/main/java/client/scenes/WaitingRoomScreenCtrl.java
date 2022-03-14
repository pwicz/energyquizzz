package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import javafx.event.ActionEvent;

public class WaitingRoomScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @Inject
    public WaitingRoomScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void leave(ActionEvent actionEvent) {
        mainCtrl.showSplash();
    }

    public void start(ActionEvent actionEvent) {
        mainCtrl.startMultiplayer();
    }
}
