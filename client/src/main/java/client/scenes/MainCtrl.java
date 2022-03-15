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
package client.scenes;

import client.utils.ServerUtils;
import commons.ServerMessage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private final ServerUtils server;

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private SplashScreenCtrl splashScreenCtrl;
    private Scene splash;

    private Scene question;
    private  MultiplayerScreenCtrl multiplayerScreenCtrl;

    private Scene singleLeaderboard;
    private SingleplayerLeaderboardCtrl singleplayerLeaderboardCtrl;

    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<SplashScreenCtrl, Parent> splash,
                           Pair<MultiplayerScreenCtrl, Parent> question, Pair<SingleplayerLeaderboardCtrl,
                           Parent> singleLeaderboard) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.splash = new Scene(splash.getValue());
        this.splashScreenCtrl = splash.getKey();

        this.question = new Scene(question.getValue());
        this.multiplayerScreenCtrl = question.getKey();

        this.singleLeaderboard = new Scene(singleLeaderboard.getValue());
        this.singleplayerLeaderboardCtrl = singleLeaderboard.getKey();

        showOverview();
        primaryStage.show();

        Long clientID = 233L; // hardcoded: we need to somehow get it from the server

        server.registerForMessage("topic/client/" + clientID, ServerMessage.class, m -> {
            handleServerMessage(m);
        });
    }

    public void handleServerMessage(ServerMessage msg){
        switch(msg.type){
            case NEW_MULTIPLAYER_GAME:
                // do something
                break;
            case NEW_SINGLEPLAYER_GAME:
                // do something else
                break;
            default:
                // invalid msg type
        }
    }

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showSplash() {
        primaryStage.setTitle("SplashScreen");
        primaryStage.setScene(splash);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showMultiplayerScreen() {
        primaryStage.setTitle("Multiplayer");
        primaryStage.setScene(question);
        multiplayerScreenCtrl.decreaseTime();
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showSingleLeaderboardScreen() {
        primaryStage.setTitle("SingleplayerLeaderboard");
        primaryStage.setScene(singleLeaderboard);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }




}