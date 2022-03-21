package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.scene.Scene;

public class LeaveCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Scene previous;


    @Inject
    public LeaveCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void stay(){
        mainCtrl.stay(previous);
    }
    public void leave(){
        mainCtrl.showSplash();
    }

    public void setPrevious(Scene previous) {
        this.previous = previous;
    }

    public Scene getPrevious() {
        return previous;
    }
}
