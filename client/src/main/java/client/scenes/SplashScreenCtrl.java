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
        mainCtrl.showOverview();
    }

    public void showMultiplayerScreen(){
        mainCtrl.showMultiplayerScreen();
    }


}
