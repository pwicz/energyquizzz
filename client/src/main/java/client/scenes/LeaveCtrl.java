package client.scenes;

import client.utils.OnLeaveAction;
import com.google.inject.Inject;
import javafx.scene.Scene;

public class LeaveCtrl {

    private final MainCtrl mainCtrl;

    private OnLeaveAction beforeLeave;
    private OnLeaveAction afterLeave;
    private Scene target;

    @Inject
    public LeaveCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void stay(){
        mainCtrl.closePopup();
    }

    public void leave(){
        if(beforeLeave != null)
            beforeLeave.doSomething();

        mainCtrl.closePopup();

        if(afterLeave == null){
            // close the application
            mainCtrl.getPrimaryStage().close();
        }
        else
            afterLeave.doSomething();
    }

    public void setTarget(Scene target) {
        this.target = target;
    }

    public void setBeforeLeave(OnLeaveAction beforeLeave) {
        this.beforeLeave = beforeLeave;
    }

    public void setAfterLeave(OnLeaveAction afterLeave) {
        this.afterLeave = afterLeave;
    }
}
