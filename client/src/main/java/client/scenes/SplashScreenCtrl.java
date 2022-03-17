package client.scenes;


import com.google.inject.Inject;

import client.utils.ServerUtils;


public class SplashScreenCtrl {


    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @Inject
    public SplashScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void leave(){
        mainCtrl.showLeave(mainCtrl.getSplash());
    }

    public void showWaitingRoom(){
        mainCtrl.showWaitingRoom();
    }

    public void showSingleplayerScreen() {
        mainCtrl.showSingleLeaderboardScreen();
    }


}
