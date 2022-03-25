package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class InputNameScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    TextField textBox;

    @FXML
    TextField serverBox;

    @FXML
    Text connectionFailed;

    @Inject
    public InputNameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void join(){

        if(textBox.getText().equals(""))
            return;
        if(serverBox.getText().equals(""))
            return;
        ClientMessage msg= new ClientMessage(ClientMessage.Type.INIT_MULTIPLAYER);

        msg.playerID = mainCtrl.getClientID();
        //place holder for when we implement waiting room
        msg.playerName = textBox.getText();
        mainCtrl.setName(msg.playerName);

        try{
        msg.serverName = serverBox.getText();
        mainCtrl.setServer(msg.serverName);
        server.setServer(msg.serverName);
        server.send("/app/general", msg);
        }
        catch (Exception e){
            connectionFailed.setVisible(true);
        }
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getInputName());
    }
}
