package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

public class MultiplayerScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    ProgressBar timeBar;

    double progress;
    Thread t;

    @Inject
    public MultiplayerScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void leave(){
        mainCtrl.showOverview();
    }

    public void decreaseTime(){
        if(t != null)
            t.interrupt();
        progress = 1.00;
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (progress>=0.00) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            timeBar.setProgress(progress);
                            progress -= 0.01;
                        }
                    });
                    Thread.sleep(100);
                }
                return null;
            }
        };

        t = new Thread(task);
        t.start();
    }

}
