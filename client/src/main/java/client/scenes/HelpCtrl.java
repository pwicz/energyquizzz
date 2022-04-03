package client.scenes;

import javax.inject.Inject;

public class HelpCtrl {

    private final MainCtrl mainCtrl;

    @Inject
    public HelpCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void leave() {
        mainCtrl.closePopup();
    }
}
