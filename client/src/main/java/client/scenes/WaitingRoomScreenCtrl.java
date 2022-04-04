package client.scenes;
import com.google.inject.Inject;

import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class WaitingRoomScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    ListView<String> playerList;

    @Inject
    public WaitingRoomScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void start(){
        mainCtrl.getServer().send("/app/general",
                new ClientMessage(ClientMessage.Type.START_MULTIPLAYER, mainCtrl.getClientID(), mainCtrl.getGameID()));
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl::showSplash, () -> mainCtrl.getServer().send("/app/general",
                new ClientMessage(ClientMessage.Type.QUIT_WAITING_ROOM, mainCtrl.getClientID(), mainCtrl.getGameID())));
    }

    public void updatePlayerList(List<String> names){
        playerList.getItems().clear();
        playerList.getItems().addAll(names);
    }

}
