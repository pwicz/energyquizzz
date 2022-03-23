package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.ClientMessage;

public class WaitingRoomScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @Inject
    public WaitingRoomScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void start(){
        server.send("/app/general",
                new ClientMessage(ClientMessage.Type.INIT_QUESTION, mainCtrl.getClientID(), mainCtrl.getGameID()));
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getWaitingRoom());
    }
}
