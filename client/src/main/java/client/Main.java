 /*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.AddQuoteCtrl;
import client.scenes.MainCtrl;
import client.scenes.QuoteOverviewCtrl;
import client.scenes.SingleplayerLeaderboardCtrl;
import client.scenes.SingleplayerScreenCtrl;
import client.scenes.WaitingRoomScreenCtrl;
import client.scenes.InBetweenScoreCtrl;
import com.google.inject.Injector;

import client.scenes.SplashScreenCtrl;
import client.scenes.MultiplayerScreenCtrl;
import client.scenes.LeaveCtrl;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var splashScreen = FXML.load(SplashScreenCtrl.class, "client","scenes", "Splash.fxml");

        var adminPanel = FXML.load(AdminPanelCtrl.class, "client","scenes", "AdminPanel.fxml");
        var editActivity = FXML.load(EditActivityCtrl.class, "client","scenes", "EditActivity.fxml");
        var createActivity = FXML.load(CreateActivityCtrl.class, "client","scenes", "CreateActivity.fxml");


        var singleplayerLeaderboard =
                FXML.load(SingleplayerLeaderboardCtrl.class, "client","scenes", "SingleplayerLeaderboard.fxml");
        var singleplayerGame = FXML.load(SingleplayerScreenCtrl.class,
                "client","scenes", "Singleplayer_Game_Screen.fxml");
        var inputname = FXML.load(InputNameScreenCtrl.class,
                "client","scenes", "InputNameScreen.fxml");

                "client","scenes", "SingleplayerGame.fxml");

        var waitingRoom = FXML.load(WaitingRoomScreenCtrl.class, "client","scenes", "WaitingRoom.fxml");
        var multiplayerGame =
                FXML.load(MultiplayerScreenCtrl.class, "client","scenes", "MultiplayerGame.fxml");
        var inBetweenScore = FXML.load(InBetweenScoreCtrl.class, "client", "scenes", "InBetweenScores.fxml");

        var leave = FXML.load(LeaveCtrl.class, "client", "scenes", "Leave.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        mainCtrl.initialize(primaryStage, splashScreen, adminPanel, editActivity, createActivity,
                singleplayerLeaderboard, singleplayerGame, waitingRoom, multiplayerGame, inBetweenScore, leave);

    }
}