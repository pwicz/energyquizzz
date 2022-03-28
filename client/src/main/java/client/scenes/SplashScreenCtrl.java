package client.scenes;


import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class SplashScreenCtrl {

    private final MainCtrl mainCtrl;

    @FXML
    Text message;

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
            // TODO: message.setStyle() for some reason doesn't work
            message.setText("Connected to " + mainCtrl.getServer().getServerURL());
            message.setStyle("-fx-fill: #38c768;");
        }
        else{
            message.setText("Not connected to a server");
            message.setStyle("-fx-fill: #e0503d;");
        }

        message.setStyle("visibility: visible;");
    }
    
    public void showAdminPanel() {
        mainCtrl.showAdminPanel();
    }

    public void render(){
        showConnectionStatus();
    }
}
