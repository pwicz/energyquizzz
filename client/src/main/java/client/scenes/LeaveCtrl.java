package client.scenes;

import client.utils.BeforeLeave;
import com.google.inject.Inject;

public class LeaveCtrl {

    private final MainCtrl mainCtrl;

    private BeforeLeave beforeLeave;

    @Inject
    public LeaveCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void stay(){
        mainCtrl.closePopup();
    }

    public void leave(){
        if(beforeLeave != null)
            beforeLeave.soSomething();

        mainCtrl.closePopup();
        mainCtrl.showSplash();
    }

    public void setBeforeLeave(BeforeLeave beforeLeave) {
        this.beforeLeave = beforeLeave;
    }
}
