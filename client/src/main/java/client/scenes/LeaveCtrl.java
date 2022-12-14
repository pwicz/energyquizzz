package client.scenes;

import client.utils.OnLeaveAction;
import com.google.inject.Inject;

public class LeaveCtrl {

    private final MainCtrl mainCtrl;

    private OnLeaveAction beforeLeave;
    private OnLeaveAction afterLeave;

    @Inject
    public LeaveCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void stay(){
        mainCtrl.closePopup();
    }

    public void leave(){
        if(beforeLeave != null) {
            try {
                beforeLeave.doSomething();
            }catch (Exception e ){
                mainCtrl.resetServer();
                mainCtrl.showSplash();

                if(afterLeave == null){
                    // close the application
                    mainCtrl.cleanupAndClose();
                    mainCtrl.closePopup();
                    return;
                }

                mainCtrl.closePopup();
                return;
            }
        }

        mainCtrl.closePopup();

        if(afterLeave == null){
            // close the application
            mainCtrl.cleanupAndClose();
        }
        else
            afterLeave.doSomething();
    }

    public void setBeforeLeave(OnLeaveAction beforeLeave) {
        this.beforeLeave = beforeLeave;
    }

    public void setAfterLeave(OnLeaveAction afterLeave) {
        this.afterLeave = afterLeave;
    }
}
