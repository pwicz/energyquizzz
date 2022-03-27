package client.scenes;


import com.google.inject.Inject;

public class SplashScreenCtrl {

    private final MainCtrl mainCtrl;

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


}
