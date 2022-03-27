package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;


public class WaitingRoomScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    ListView<String> playerList;

    @Inject
    public WaitingRoomScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void start(){
        server.send("/app/general",
                new ClientMessage(ClientMessage.Type.START_MULTIPLAYER, mainCtrl.getClientID(), mainCtrl.getGameID()));
    }

    public void leave(){
        mainCtrl.showLeaveWaitingroom(mainCtrl.getWaitingRoom(), () -> server.send("/app/general",
                new ClientMessage(ClientMessage.Type.QUIT_WAITING_ROOM, mainCtrl.getClientID(), mainCtrl.getGameID())));
    }

    public void updatePlayerList(List<String> names){
        playerList.getItems().clear();
        playerList.getItems().addAll(names);
    }
}
