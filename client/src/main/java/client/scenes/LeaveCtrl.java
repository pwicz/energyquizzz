package client.scenes;

import client.utils.BeforeLeave;
import client.utils.ServerUtils;
import com.google.inject.Inject;

public class LeaveCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private BeforeLeave beforeLeave;

    @Inject
    public LeaveCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void stay(){
        mainCtrl.closePopup();
    }

    public void leave(){
        if(beforeLeave != null){
            beforeLeave.soSomething();
            mainCtrl.showSplash();
        }

        mainCtrl.closePopup();
        mainCtrl.showSplash();
    }


    public void setBeforeLeave(BeforeLeave beforeLeave) {
        this.beforeLeave = beforeLeave;
    }
}
