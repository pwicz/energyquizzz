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
    Text connectionFailed;

    @Inject
    public InputNameScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void join(){
        if(textBox.getText().equals("")) return;

        ClientMessage msg = new ClientMessage(ClientMessage.Type.INIT_MULTIPLAYER);

        msg.playerID = mainCtrl.getClientID();
        //place holder for when we implement waiting room
        msg.playerName = textBox.getText();
        mainCtrl.setName(msg.playerName);

        mainCtrl.getServer().send("/app/general", msg);
    }

    public void leave(){
        mainCtrl.showSplash();
    }
}
