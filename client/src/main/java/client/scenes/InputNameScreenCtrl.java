package client.scenes;
import com.google.inject.Inject;

import commons.ClientMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class InputNameScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    TextField textBox;

    @FXML
    Label nameTaken;

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

    public void nameTaken(){
        nameTaken.setVisible(false);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nameTaken.setVisible(true);
    }

    public void resetNameTaken(){
        nameTaken.setVisible(false);
    }
}
