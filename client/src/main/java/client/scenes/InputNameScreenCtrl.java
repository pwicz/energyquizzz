package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class InputNameScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    TextField textBox;


    @Inject
    public InputNameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void join(){

        if(textBox.getText().equals(""))
            return;
        ClientMessage msg= new ClientMessage(ClientMessage.Type.INIT_MULTIPLAYER);

        msg.playerID = mainCtrl.getClientID();
        //place holder for when we implement waiting room
        msg.playerName = textBox.getText();
        mainCtrl.setName(msg.playerName);

        server.send("/app/general", msg);
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getInputName());
    }
}
