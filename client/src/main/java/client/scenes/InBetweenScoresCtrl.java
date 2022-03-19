package client.scenes;
import com.google.inject.Inject;
import client.utils.ServerUtils;

public class InBetweenScoresCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @Inject
    public InBetweenScoresCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

}
