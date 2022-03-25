package client.scenes;

import client.utils.BeforeLeave;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.scene.Scene;

public class LeaveCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Scene previous;
    private BeforeLeave beforeLeave;


    @Inject
    public LeaveCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void stay(){
        mainCtrl.stay();
    }

    public void leave(){
        if(beforeLeave != null) {
            beforeLeave.soSomething();
            mainCtrl.showSplash();
        }
        if(previous.equals(mainCtrl.getSplash())) {
            mainCtrl.getPrimaryStage().close();
            mainCtrl.stay();
        }else if(previous.equals(mainCtrl.getSingleplayerScreen())){
            mainCtrl.leaveSingleplayer();
        }
        mainCtrl.showSplash();
        mainCtrl.stay();
    }

    public void setPrevious(Scene previous) {
        this.previous = previous;
    }

    public void setBeforeLeave(BeforeLeave beforeLeave) {
        this.beforeLeave = beforeLeave;
    }

    public Scene getPrevious() {
        return previous;
    }
}
