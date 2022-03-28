package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class InputServerScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    TextField serverBox;

    @FXML
    Text connectionFailed;

    @FXML
    Button leave;

    @Inject
    public InputServerScreenCtrl(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }

    public void render(boolean isClientConnected){
        serverBox.setText("http://localhost:8080/");
        if(!isClientConnected) hideLeaveButton();
    }

    public void join(){

        String url = serverBox.getText();
        if(url == null || url.isEmpty()){
            connectionFailed.setVisible(true);
            return;
        }

        if(!mainCtrl.connectToServer(url)){
            connectionFailed.setVisible(true);
            return;
        }

        mainCtrl.closePopup();
    }

    public void leave(){
        connectionFailed.setVisible(true);
        mainCtrl.closePopup();
    }

    public void hideLeaveButton(){
        leave.setStyle("visibility: hidden");
    }
}
