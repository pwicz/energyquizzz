package client.scenes;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class InputServerScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    TextField serverBox;

    @FXML
    Text connectionFailed;

    @Inject
    public InputServerScreenCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void join(){
        if(serverBox.getText().equals(""))
            return;
        //connect to next screen
        //display error message
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getInputName());
    }
}
