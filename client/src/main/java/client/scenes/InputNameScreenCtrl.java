package client.scenes;
import com.google.inject.Inject;

import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class InputNameScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    TextField textBox;

    @FXML
    TextField serverBox;

    @FXML
    Text connectionFailed;

    @Inject
    public InputNameScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
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
        mainCtrl.setServerName(msg.serverName);
        mainCtrl.getServer().setServer(msg.serverName);
        mainCtrl.getServer().send("/app/general", msg);
        }
        catch (Exception e){
            connectionFailed.setVisible(true);
        }
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getInputName());
    }
}
