package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;

public class WaitingRoomScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @Inject
    public WaitingRoomScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void startGame(){
        mainCtrl.showMultiplayerScreen();
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getWaitingRoom());
    }

    public void start() {

    }
}
