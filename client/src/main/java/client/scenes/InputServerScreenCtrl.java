package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class InputServerScreenCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    TextField serverBox;

    @FXML
    Text connectionFailed;

    @Inject
    public InputServerScreenCtrl(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }

    public void join(){

        String url = serverBox.getText();
        if(url == null || url.isEmpty()){
            // render error
            return;
        }

        if(!mainCtrl.connectToServer(url)){
            // render error
            return;
        }

        mainCtrl.closePopup();
    }

    public void leave(){

        mainCtrl.closePopup();

    }
}
