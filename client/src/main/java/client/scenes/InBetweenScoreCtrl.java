package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class InBetweenScoreCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @Inject
    public InBetweenScoreCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getInBetweenScore());
    }
}
