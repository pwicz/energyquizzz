package client.scenes;


import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class SplashScreenCtrl {

    private final MainCtrl mainCtrl;

    @FXML
    Text noConnection;

    @FXML
    Text connected;

    @Inject
    public SplashScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getSplash());
    }

    public void showWaitingRoom(){

        mainCtrl.showinputNameScreen();
    }

    public void showSingleplayerScreen() {
        mainCtrl.showSingleLeaderboardScreen();
    }

    public void showConnectionStatus(){
        if(mainCtrl.checkServerConnection()){
            connected.setVisible(true);
        }
        if(!mainCtrl.checkServerConnection()){
            noConnection.setVisible(true);
        }
    }
    
    public void showAdminPanel() {
        mainCtrl.showAdminPanel();
    }
}
